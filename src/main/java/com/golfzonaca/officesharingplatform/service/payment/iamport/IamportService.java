package com.golfzonaca.officesharingplatform.service.payment.iamport;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.Refund;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.*;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import com.golfzonaca.officesharingplatform.service.payment.kakaopay.KakaoPayUtility;
import com.golfzonaca.officesharingplatform.service.refund.RefundService;
import com.golfzonaca.officesharingplatform.web.payment.form.NicePayRequestForm;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
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
import java.util.*;

@RequiredArgsConstructor
@Transactional
@Service
public class IamportService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
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

        if (findReservation.getRoom().getRoomKind().getRoomType().toString().contains("OFFICE")) {
            IamportResponse<List<Schedule>> iamportListResponse = requestNicePaySubscribe(userId, nicePayRequestForm);
            if (iamportListResponse.getCode() != 0) {
                return "localhost:3000/place/" + findPlaceId.toString();
            }
        } else {
            if (PayType.DEPOSIT.toString().equals(nicePayRequestForm.getPayWay())) {
                IamportResponse<Payment> iamportResponse = requestNicePayOnetime(userId, nicePayRequestForm);
                if (iamportResponse.getCode() != 0) {
                    return "localhost:3000/place/" + findPlaceId.toString();
                }
                nicePayRequestForm.changePayTypeAndPayWay(PayType.BALANCE.toString(), PayWay.POSTPAYMENT.toString());
                IamportResponse<List<Schedule>> paymentTypeAndWayResponse = requestNicePaySubscribe(userId, nicePayRequestForm);
                if (paymentTypeAndWayResponse.getCode() != 0) {
                    return "localhost:3000/place/" + findPlaceId.toString();
                }
            } else {
                IamportResponse<Payment> iamportResponseFinally = requestNicePayOnetime(userId, nicePayRequestForm);
                if (iamportResponseFinally.getCode() != 0) {
                    return "localhost:3000/place/" + findPlaceId.toString();
                }
            }
        }
        return "localhost:3000/mypage/" + findReservation.getId();
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

        String mid = createMerchantUid(); // 상점아이디 만들어줌

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(mid, new BigDecimal(totalAmount), cardInfo); // onetime 결제 요청
        onetimePaymentData.setPg("nice");

        //결제 요청을 데이터 가공
        com.golfzonaca.officesharingplatform.domain.Payment payment = processingPaymentData(findReservation, nicePayRequestForm.getPayWay(), nicePayRequestForm.getPayType(), nicePayRequestForm.getPayMileage(), mid);
        //paymentRepository에 저장
        com.golfzonaca.officesharingplatform.domain.Payment paymentSave = paymentRepository.save(payment);

        if (payment.getPayMileage() > 0) {
            mileageService.payingMileage(paymentSave);
        }
        IamportResponse<Payment> iamportResponse = iamportClient.onetimePayment(onetimePaymentData);
        if (PayType.FULL_PAYMENT.equals(payment.getType())) {
            mileageService.savingFullPaymentMileage(payment);
        }

        paymentSave.updatePayStatus(PaymentStatus.COMPLETED); // paystatus completed로 업데이트

        if (payment.getType().equals(PayType.DEPOSIT)) {
            requestNicePaySubscribe(userId, nicePayRequestForm); // payType이 deposit이면 requestNicePaySubscribe 호출
        }

        return iamportResponse;
    }

    public IamportResponse<List<Schedule>> requestNicePaySubscribe(Long userId, NicePayRequestForm nicePayRequestForm) throws IamportResponseException, IOException {

        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);
        Reservation findReservation = reservationRepository.findById(nicePayRequestForm.getReservationId());

        List<com.golfzonaca.officesharingplatform.domain.Payment> payments = paymentRepository.findByReservationId(nicePayRequestForm.getReservationId());

        ScheduleEntry scheduleEntry = null;

        for (com.golfzonaca.officesharingplatform.domain.Payment payment : payments) {
            if (payment.getReservation().getId().equals(nicePayRequestForm.getReservationId())) {
                if (payment.getType().equals(PayType.DEPOSIT)) {
                    LocalDateTime paymentDateTime = LocalDateTime.of(findReservation.getResStartDate(), findReservation.getResStartTime());
                    long scheduleAt = (Timestamp.valueOf(paymentDateTime).getTime());
                    Date time = new Date(scheduleAt);
                    long calculateTotalAmount = calculateTotalAmount(findReservation, String.valueOf(payment.getPayWay()), String.valueOf(payment.getType()), payment.getPayMileage());
                    long balance = calculateTotalAmount - payment.getPrice();
                    scheduleEntry = new ScheduleEntry(payment.getApiCode(), time, new BigDecimal(balance));
                }
            }
        }

        ScheduleData scheduleData = new ScheduleData(createCustomerUid());
        scheduleData.addSchedule(scheduleEntry);

        com.golfzonaca.officesharingplatform.domain.Payment payment = processingPaymentData(findReservation, nicePayRequestForm.getPayWay(), nicePayRequestForm.getPayType(), nicePayRequestForm.getPayMileage(), createMerchantUid());
        paymentRepository.save(payment);

        return iamportClient.subscribeSchedule(scheduleData);
    }

    public List<IamportResponse<Payment>> nicePayCancel(Long userId, long reservationId) throws IamportResponseException, IOException {

        Reservation findReservation = reservationRepository.findById(reservationId);
        User findUser = userRepository.findById(userId);

        List<com.golfzonaca.officesharingplatform.domain.Payment> findPayments = findReservation.getPaymentList();

        restoreUserMileage(findUser, findPayments);

        List<Refund> refunds = refundService.processingRefundData(findPayments);

        List<IamportResponse<Payment>> iamportResponses = refundRequest(refunds);

        findReservation.updateStatus(ReservationStatus.CANCELED, FixStatus.CANCELED);

        return iamportResponses;
    }

    public List<IamportResponse<Payment>> refundRequest(List<Refund> refunds) throws IamportResponseException, IOException {

        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);

        List<IamportResponse<Payment>> refundResult = new LinkedList<>();

        for (Refund refund : refunds) {
            int cancelAmount = (int) refund.getRefundPrice();
            CancelData cancelData = new CancelData(refund.getPayment().getApiCode(), false, new BigDecimal(cancelAmount));
            IamportResponse<Payment> iamportResponse = iamportClient.cancelPaymentByImpUid(cancelData);
            refundResult.add(iamportResponse);
            refund.updateRefundStatus(true);
            refund.getPayment().updatePayStatus(PaymentStatus.CANCELED);
        }
        return refundResult;
    }

    public void restoreUserMileage(User user, List<com.golfzonaca.officesharingplatform.domain.Payment> findPayment) {

        Mileage getUserMileage = user.getMileage();

        for (com.golfzonaca.officesharingplatform.domain.Payment payment : findPayment) {
            getUserMileage.addPoint(payment.getPayMileage());
            mileageService.recoveryMileage(getUserMileage, payment);
        }
    }

    public String createMerchantUid() {
        return ("N" + UUID.randomUUID().toString().replaceAll("-", "")).substring(0, 20);
    }

    private com.golfzonaca.officesharingplatform.domain.Payment processingPaymentData(Reservation reservation, String payWay, String payType, long payMileage, String apiCode) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        Integer totalAmount = kakaoPayUtility.calculateTotalAmount(reservation, payWay, payType, payMileage);

        return com.golfzonaca.officesharingplatform.domain.Payment.builder()
                .reservation(reservation)
                .payDate(LocalDate.now())
                .payTime(LocalTime.now())
                .price(totalAmount)
                .payMileage(payMileage)
                .payWay(PayWay.valueOf(payWay))
                .savedMileage(kakaoPayUtility.calculateMileage(totalAmount, payWay, payType))
                .type(PayType.valueOf(payType))
                .apiCode(apiCode)
                .pg(PG.NICEPAY)
                .payStatus(PaymentStatus.PROGRESSING)
                .build();
    }

    public Integer calculateTotalAmount(Reservation reservation, String payWay, String payType, long payMileage) {

        int totalAmount;
        int payPrice;

        if (reservation.getRoom().getRoomKind().getRoomType().toString().contains("OFFICE")) {
            return (int) Math.abs((ChronoUnit.DAYS.between(reservation.getResEndDate(), reservation.getResStartDate()) * reservation.getRoom().getRoomKind().getPrice()));
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

    public String createCustomerUid() {
        return ("N" + UUID.randomUUID().toString().replaceAll("-", "")).substring(0, 20);
    }
}
