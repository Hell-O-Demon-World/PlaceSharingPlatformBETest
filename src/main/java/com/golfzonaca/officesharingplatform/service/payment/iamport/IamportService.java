package com.golfzonaca.officesharingplatform.service.payment.iamport;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.Refund;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.*;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.payment.kakaopay.KakaoPayUtility;
import com.golfzonaca.officesharingplatform.service.refund.RefundService;
import com.golfzonaca.officesharingplatform.web.payment.dto.kakaopay.KakaoPayCancelResponse;
import com.golfzonaca.officesharingplatform.web.payment.form.NicePayRequestForm;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
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
    private final UserRepository userRepository;
    private final RefundService refundService;
    private final PaymentRepository paymentRepository;

    @Value("${iamport.api.apiKey}")
    private String apiKey;

    @Value("${iamport.api.apiSecret}")
    private String apiSecret;

    public IamportResponse<Payment> requestNicePay(Long userId, NicePayRequestForm nicePayRequestForm) throws IamportResponseException, IOException {

        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);

        Reservation findReservation = reservationRepository.findById(nicePayRequestForm.getReservationId());

        int totalAmount = calculateTotalAmount(findReservation,
                nicePayRequestForm.getPayWay(),
                nicePayRequestForm.getPayType(),
                nicePayRequestForm.getPayMileage());

//        int totalAmount = 1000;
        CardInfo cardInfo = new CardInfo(nicePayRequestForm.getCard_number(),
                nicePayRequestForm.getExpiry(),
                nicePayRequestForm.getBirth(),
                nicePayRequestForm.getPwd_2digit());

        String mid = createMerchantUid();
        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(mid, new BigDecimal(totalAmount), cardInfo);
        onetimePaymentData.setPg("nice");

        com.golfzonaca.officesharingplatform.domain.Payment payment = processingPaymentData(findReservation, nicePayRequestForm.getPayWay(), nicePayRequestForm.getPayType(), nicePayRequestForm.getPayMileage(), mid);
        com.golfzonaca.officesharingplatform.domain.Payment paymentSave = paymentRepository.save(payment);

        IamportResponse<Payment> iamportResponse = iamportClient.onetimePayment(onetimePaymentData);
        paymentSave.updatePayStatus(PaymentStatus.COMPLETED);


        return iamportResponse;
    }

    public List<IamportResponse<Payment>> nicePayCancel(Long userId, long reservationId) throws IamportResponseException, IOException {

        Reservation findReservation = reservationRepository.findById(reservationId);
        User findUser = userRepository.findById(userId);

        List<com.golfzonaca.officesharingplatform.domain.Payment> findPayments = findReservation.getPaymentList();

        restoreUserMileage(findUser, findPayments);

        List<Refund> refunds = refundService.processingRefundData(findPayments);

        List<IamportResponse<Payment>> iamportResponses = refundRequest(refunds);

        findReservation.updateStatus(ReservationStatus.CANCELED);

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
            getUserMileage.minusPoint(payment.getSavedMileage());
        }
    }

    public String createMerchantUid() {
        return ("N" + UUID.randomUUID().toString().replaceAll("-", "")).substring(0, 19);
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


    public Integer calculateTaxFreeAmount(Integer totalAmount) {
        return (totalAmount) * 10 / 11;
    }

    public Integer calculateVatAmount(Integer totalAmount) {
        return totalAmount / 11;
    }

    public long calculateMileage(Integer totalAmount, String payWay, String payType) {

        if (payWay.equals(PayWay.PREPAYMENT.toString()) && payType.equals(PayType.FULLPAYMENT.toString())) {
            return (long) (totalAmount * 0.05);
        } else {
            return 0;
        }
    }
}