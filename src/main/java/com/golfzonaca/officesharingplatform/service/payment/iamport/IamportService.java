package com.golfzonaca.officesharingplatform.service.payment.iamport;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.Refund;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.*;
import com.golfzonaca.officesharingplatform.exception.NonExistedMileageException;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import com.golfzonaca.officesharingplatform.service.payment.kakaopay.KakaoPayUtility;
import com.golfzonaca.officesharingplatform.service.refund.RefundService;
import com.golfzonaca.officesharingplatform.web.payment.form.NicePayRequestForm;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class IamportService {

    private final ReservationRepository reservationRepository;
    private final RefundService refundService;
    private final MileageService mileageService;
    private final PaymentRepository paymentRepository;

    @Value("${iamport.api.apiKey}")
    private String apiKey;

    @Value("${iamport.api.apiSecret}")
    private String apiSecret;

    public String requestNicePay(Long userId, NicePayRequestForm nicePayRequestForm) throws IamportResponseException, IOException {

        Reservation findReservation = reservationRepository.findById(nicePayRequestForm.getReservationId());
        Long findPlaceId = findReservation.getRoom().getPlace().getId();
        String redirectUrlPayFailure = "https://office-sharing-platform.vercel.app/place/" + findPlaceId.toString();
        String redirectUrlPaySuccess = "https://office-sharing-platform.vercel.app/mypage/" + findReservation.getId();

        if (findReservation.getRoom().getRoomKind().getRoomType().toString().contains("OFFICE")) {
            IamportResponse<List<Schedule>> iamportListResponse = requestNicePaySubscribe(userId, nicePayRequestForm);
            if (iamportListResponse.getCode() != 0) {
                return redirectUrlPayFailure;
            }
        } else {
            if (PayType.DEPOSIT.toString().equals(nicePayRequestForm.getPayType())) {
                IamportResponse<Payment> iamportResponse = requestNicePayOnetime(userId, nicePayRequestForm);
                if (iamportResponse.getCode() != 0) {
                    return redirectUrlPayFailure;
                }
                nicePayRequestForm.changePayTypeAndPayWay(PayType.BALANCE.toString(), PayWay.POSTPAYMENT.toString());
                IamportResponse<List<Schedule>> paymentTypeAndWayResponse = requestNicePaySubscribe(userId, nicePayRequestForm);
                if (paymentTypeAndWayResponse.getCode() != 0) {
                    return redirectUrlPayFailure;
                }
            } else {
                IamportResponse<Payment> iamportResponseFinally = requestNicePayOnetime(userId, nicePayRequestForm);
                if (iamportResponseFinally.getCode() != 0) {
                    return redirectUrlPayFailure;
                }
            }
        }
        findReservation.updateResStatus(ReservationStatus.COMPLETED);
        return redirectUrlPaySuccess;
    }

    public IamportResponse<Payment> requestNicePayOnetime(Long userId, NicePayRequestForm nicePayRequestForm) throws IamportResponseException, IOException {

        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);
        Reservation findReservation = reservationRepository.findById(nicePayRequestForm.getReservationId());

        int totalAmount = calculateTotalAmount(findReservation, //총액 정보 가져옴
                nicePayRequestForm.getPayWay(),
                nicePayRequestForm.getPayType(),
                nicePayRequestForm.getPayMileage());

        CardInfo cardInfo = new CardInfo(nicePayRequestForm.getCard_number(), // 카드 정보 가져옴
                nicePayRequestForm.getExpiry(),
                nicePayRequestForm.getBirth(),
                nicePayRequestForm.getPwd_2digit());

        String merchantUid = createMerchantUid(); // 상점아이디 만들어줌

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, new BigDecimal(totalAmount), cardInfo); // onetime 결제 요청
        onetimePaymentData.setPg("nice");

        //결제 요청을 데이터 가공
        com.golfzonaca.officesharingplatform.domain.Payment payment = processingPaymentData(findReservation, nicePayRequestForm.getPayWay(), nicePayRequestForm.getPayType(), nicePayRequestForm.getPayMileage(), merchantUid);
        //paymentRepository에 저장
        com.golfzonaca.officesharingplatform.domain.Payment paymentSave = paymentRepository.save(payment);

        Mileage userMileage = paymentSave.getReservation().getUser().getMileage();
        long userMileagePoint = userMileage.getPoint();
        if (payment.getPayMileage() > 0) {
            if (userMileagePoint < payment.getPayMileage()) {
                throw new NonExistedMileageException("가지고 있는 마일리지보다 사용할 마일리지가 클 수 없습니다.");
            }
            mileageService.payingMileage(payment);
        }
        IamportResponse<Payment> iamportResponse = iamportClient.onetimePayment(onetimePaymentData);
        payment.addReceipt(iamportResponse.getResponse().getReceiptUrl());
        paymentSave.updatePayStatus(PaymentStatus.COMPLETED); // paystatus completed로 업데이트

        return iamportResponse;
    }

    public IamportResponse<List<Schedule>> requestNicePaySubscribe(Long userId, NicePayRequestForm nicePayRequestForm) throws IamportResponseException, IOException {

        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);
        Reservation reservation = reservationRepository.findById(nicePayRequestForm.getReservationId());

        String customerUid = createCustomerUid(nicePayRequestForm);
        ScheduleData scheduleData = new ScheduleData(customerUid);

        String merchantUid = createMerchantUid();

        Timestamp scheduleAt;
        if (reservation.getRoom().getRoomKind().getRoomType().toString().contains("OFFICE")) {
            LocalDateTime payScheduledDateTime = LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime());
            scheduleAt = Timestamp.valueOf(payScheduledDateTime);
        } else {
            LocalDateTime payScheduledDateTime = LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime());
            scheduleAt = Timestamp.valueOf(payScheduledDateTime);
        }

        Integer payPrice = calculateTotalAmount(reservation, nicePayRequestForm.getPayWay(), nicePayRequestForm.getPayType(), nicePayRequestForm.getPayMileage());

        ScheduleEntry scheduleEntry = new ScheduleEntry(merchantUid, scheduleAt, BigDecimal.valueOf(payPrice));
        scheduleEntry.setName("OSP " + reservation.getRoom().getRoomKind().getRoomType().getDescription() + " 후불 결제");
        scheduleData.addSchedule(scheduleEntry);
        com.golfzonaca.officesharingplatform.domain.Payment payment = processingPaymentData(reservation, nicePayRequestForm.getPayWay(), nicePayRequestForm.getPayType(), nicePayRequestForm.getPayMileage(), merchantUid);
        IamportResponse<List<Schedule>> listIamportResponse = iamportClient.subscribeSchedule(scheduleData);
        paymentRepository.save(payment);
        return listIamportResponse;
    }

    public String nicePayCancel(Long userId, long reservationId) throws IamportResponseException, IOException {
        Reservation reservation = reservationRepository.findById(reservationId);
        List<com.golfzonaca.officesharingplatform.domain.Payment> paymentList = paymentRepository.findProgressingPaymentByReservation(reservation);
        for (com.golfzonaca.officesharingplatform.domain.Payment payment : paymentList) {
            if (payment.getPayWay().equals(PayWay.PREPAYMENT)) {
                List<IamportResponse<Payment>> iamportResponses = nicePayCancelOneTime(payment);
            } else {
                IamportResponse<List<Schedule>> listIamportResponse = nicePayCancelSubscribe(payment);
            }
        }
        reservation.updateAllStatus(ReservationStatus.CANCELED, FixStatus.CANCELED);
        return "https://office-sharing-platform.vercel.app/mypage/" + reservation.getId();
    }

    private IamportResponse<List<Schedule>> nicePayCancelSubscribe(com.golfzonaca.officesharingplatform.domain.Payment payment) throws IamportResponseException, IOException {
        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);
        User user = payment.getReservation().getUser();
        String userEmail = user.getEmail();
        Reservation reservation = payment.getReservation();
        String customerUid = userEmail.replace("@", "").replace(".", "") + reservation.getId();
        UnscheduleData unscheduleData = new UnscheduleData(customerUid);

        String merchantUid = payment.getApiCode();

        unscheduleData.addMerchantUid(merchantUid);
        IamportResponse<List<Schedule>> listIamportResponse = iamportClient.unsubscribeSchedule(unscheduleData);
        paymentRepository.delete(payment);
        return listIamportResponse;
    }

    public List<IamportResponse<Payment>> nicePayCancelOneTime(com.golfzonaca.officesharingplatform.domain.Payment payment) throws IamportResponseException, IOException {
//        restoreUserMileage(payment);
        Refund refunds = refundService.processingRefundData(payment);
        List<IamportResponse<Payment>> iamportResponses = refundRequest(refunds);
        return iamportResponses;
    }

    public List<IamportResponse<Payment>> refundRequest(Refund refund) throws IamportResponseException, IOException {
        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);

        List<IamportResponse<Payment>> refundResult = new LinkedList<>();

        int cancelAmount = (int) refund.getRefundPrice();
        CancelData cancelData = new CancelData(refund.getPayment().getApiCode(), false, new BigDecimal(cancelAmount));
        IamportResponse<Payment> iamportResponse = iamportClient.cancelPaymentByImpUid(cancelData);
        refundResult.add(iamportResponse);
        refund.updateRefundStatus(true);
        refund.getPayment().updatePayStatus(PaymentStatus.CANCELED);

        return refundResult;
    }

    public void restoreUserMileage(com.golfzonaca.officesharingplatform.domain.Payment payment) {
        User user = payment.getReservation().getUser();
        Mileage getUserMileage = user.getMileage();

        getUserMileage.addPoint(payment.getPayMileage());
        mileageService.recoveryMileage(getUserMileage, payment);

    }

    public String createMerchantUid() {
        return ("N" + UUID.randomUUID().toString().replaceAll("-", "")).substring(0, 20);
    }

    private com.golfzonaca.officesharingplatform.domain.Payment processingPaymentData(Reservation reservation, String payWay, String payType, long payMileage, String apiCode) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        Integer totalAmount = kakaoPayUtility.calculateTotalAmount(reservation, payWay, payType, payMileage);

        return new com.golfzonaca.officesharingplatform.domain.Payment(
                reservation,
                LocalDate.now(),
                LocalTime.now(),
                totalAmount,
                payMileage,
                PayWay.valueOf(payWay),
                kakaoPayUtility.calculateMileage(totalAmount, payWay, payType),
                PayType.valueOf(payType),
                apiCode,
                PG.NICEPAY,
                PaymentStatus.PROGRESSING,
                "카카오페이에서 확인하실 수 있습니다.");
    }

    public Integer calculateTotalAmount(Reservation reservation, String payWay, String payType, long payMileage) {

        int totalAmount;
        int payPrice;

        if (reservation.getRoom().getRoomKind().getRoomType().toString().contains("OFFICE")) {
            return (int) (Math.abs((ChronoUnit.DAYS.between(reservation.getResEndDate(), reservation.getResStartDate()) * reservation.getRoom().getRoomKind().getPrice())) - payMileage);
        } else {
            totalAmount = (int) Math.abs(ChronoUnit.HOURS.between(LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime()), LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime())) * (reservation.getRoom().getRoomKind().getPrice()));
            payPrice = (int) (totalAmount - payMileage);
        }

        if (PayWay.valueOf(payWay).equals(PayWay.PREPAYMENT)) {
            if (PayType.valueOf(payType).equals(PayType.DEPOSIT)) {
                return (int) (payPrice * 0.2);
            } else {
                return (payPrice);
            }
        } else {
            return (int) (payPrice * 0.8);
        }
    }

    public String createCustomerUid(NicePayRequestForm nicePayRequestForm) throws IamportResponseException, IOException {
        Reservation reservation = reservationRepository.findById(nicePayRequestForm.getReservationId());
        User user = reservation.getUser();
        String userEmail = user.getEmail();
        String customerUid = userEmail.replace("@", "").replace(".", "") + reservation.getId();
        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);
        BillingCustomerData billingData = new BillingCustomerData(customerUid, nicePayRequestForm.getCard_number(), nicePayRequestForm.getExpiry(), nicePayRequestForm.getBirth());
        billingData.setPwd2Digit(nicePayRequestForm.getPwd_2digit());
        billingData.setPg(nicePayRequestForm.getPg());
        IamportResponse<BillingCustomer> billingCustomerIamportResponse = iamportClient.postBillingCustomer(customerUid, billingData);
        return customerUid;
    }
}
