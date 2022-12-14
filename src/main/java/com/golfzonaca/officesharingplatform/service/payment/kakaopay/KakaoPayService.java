package com.golfzonaca.officesharingplatform.service.payment.kakaopay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.*;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mileage.MileageService;
import com.golfzonaca.officesharingplatform.service.payment.PaymentValidation;
import com.golfzonaca.officesharingplatform.service.refund.RefundService;
import com.golfzonaca.officesharingplatform.web.payment.dto.kakaopay.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com/";

    @Value("${kakaoPay.api.apiKey}")
    private String kakaoPayApiKey;

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final MileageService mileageService;
    private final UserRepository userRepository;
    private final RefundService refundService;

    public String kakaoPayReadyRequest(Long userId, long reservationId, String payWay, String payType, long payMileage) {

        Reservation reservation = reservationRepository.findById(reservationId);
        User user = userRepository.findById(userId);

        PaymentValidation paymentValidation = new PaymentValidation();
        paymentValidation.validExistedType(payType);
        paymentValidation.validExistedPayWay(payWay);
        paymentValidation.validPairByRoomType(payType, payWay, reservation.getRoom().getRoomKind().getRoomType());
        paymentValidation.validUserForReservation(user, reservation);

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();
        HttpHeaders header = kakaoPayUtility.makeHttpHeader(kakaoPayApiKey);

        Payment payment = processingPaymentData(reservation, payWay, payType, payMileage, "");
        paymentRepository.save(payment);

        KakaoPayReadyRequest kakaoPayReadyRequest = kakaoPayUtility.makeRequestBodyForReady(payment);
        HttpEntity<MultiValueMap<String, String>> requestReadyEntity = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), kakaoPayReadyRequest), header);
        KakaoPayReadyResponse kakaoPayReadyResponse = sendKakaoPayReadyRequest(HOST, requestReadyEntity);

        payment.updateApiCode(kakaoPayReadyResponse.getTid());
        return kakaoPayReadyResponse.getNextRedirectPcUrl();
    }

    public KakaoPayApprovalResponse kakaoPayApprovalRequest(long paymentId, String pgToken) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();
        Payment payment = paymentRepository.findById(paymentId);
        if (payment.getPayMileage() > 0) {
            mileageService.payingMileage(payment);
        }

        payment.updatePayStatus(PaymentStatus.COMPLETED);
        HttpHeaders httpHeaders = kakaoPayUtility.makeHttpHeader(kakaoPayApiKey);
        KakaoPayApprovalRequest body = kakaoPayUtility.makeRequestBodyForApprove(payment, pgToken);
        HttpEntity<MultiValueMap<String, String>> requestApprovalEntity = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), body), httpHeaders);

        KakaoPayApprovalResponse kakaoPayApprovalResponse = sendKakaoPayApprovalRequest(HOST, requestApprovalEntity);
        if (PayType.FULL_PAYMENT.equals(payment.getType())) {
            mileageService.savingFullPaymentMileage(payment);
        }

        return kakaoPayApprovalResponse;
    }

    public List<KakaoPayCancelResponse> kakaoPayCancel(Long userId, Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId);
        User user = userRepository.findById(userId);

        PaymentValidation paymentValidation = new PaymentValidation();
        paymentValidation.validUserForReservation(user, reservation);

        List<Payment> findPayment = paymentValidation.cancelAvailableTimeValidation(reservation);

        restoreUserMileage(user, findPayment);

        List<Refund> refunds = refundService.processingRefundData(findPayment);

        List<KakaoPayCancelResponse> kakaoPayCancelResponses = kakaoPayCancelRequest(refunds);

        for (Refund refund : refunds) {
            refund.updateRefundStatus(true);
            refund.getPayment().updatePayStatus(PaymentStatus.CANCELED);
        }
        reservation.updateStatus(ReservationStatus.CANCELED);

        return kakaoPayCancelResponses;
    }

    public List<KakaoPayCancelResponse> kakaoPayCancelRequest(List<Refund> refunds) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        List<KakaoPayCancelResponse> cancelResult = new LinkedList<>();

        for (Refund refund : refunds) { // 검증이 끝나면 취소요청하기
            HttpHeaders httpHeaders = kakaoPayUtility.makeHttpHeader(kakaoPayApiKey);

            KakaoPayCancelRequest kakaoPayCancelRequest = kakaoPayUtility.makeRequestBodyForCancel(refund);

            HttpEntity<MultiValueMap<String, String>> requestCancelEntity = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), kakaoPayCancelRequest), httpHeaders);
            KakaoPayCancelResponse kakaoPayCancelResponse = sendKakaoPayCancelRequest(HOST, requestCancelEntity);
            cancelResult.add(kakaoPayCancelResponse);
        }
        return cancelResult;
    }

    public void restoreUserMileage(User user, List<Payment> findPayment) {
        Mileage userMileage = user.getMileage();

        for (Payment payment : findPayment) {
            userMileage.addPoint(payment.getPayMileage());
            mileageService.recoveryMileage(userMileage, payment);
        }

    }

    public KakaoPayReadyResponse sendKakaoPayReadyRequest(String host, HttpEntity<MultiValueMap<String, String>> body) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayReadyResponse kakaoPayReadyResponse = restTemplate.postForObject(new URI(host + "/v1/payment/ready"), body, KakaoPayReadyResponse.class);
            return kakaoPayReadyResponse;
        } catch (RestClientException | URISyntaxException e) {
            log.error(e.toString());
        }
        return null;
    }

    public KakaoPayApprovalResponse sendKakaoPayApprovalRequest(String host, HttpEntity<MultiValueMap<String, String>> body) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayApprovalResponse kakaoPayApprovalResponse = restTemplate.postForObject(new URI(host + "/v1/payment/approve"), body, KakaoPayApprovalResponse.class);
            return kakaoPayApprovalResponse;
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public KakaoPayCancelResponse sendKakaoPayCancelRequest(String host, HttpEntity<MultiValueMap<String, String>> body) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayCancelResponse kakaoPayCancelResponse = restTemplate.postForObject(new URI(host + "/v1/payment/cancel"), body, KakaoPayCancelResponse.class);

            return kakaoPayCancelResponse;
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private Payment processingPaymentData(Reservation reservation, String payWay, String payType, long payMileage, String apiCode) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        Integer totalAmount = kakaoPayUtility.calculateTotalAmount(reservation, payWay, payType, payMileage);

        return Payment.builder()
                .reservation(reservation)
                .payDate(LocalDate.now())
                .payTime(LocalTime.now())
                .price(totalAmount)
                .payMileage(payMileage)
                .payWay(PayWay.valueOf(payWay))
                .savedMileage(kakaoPayUtility.calculateMileage(totalAmount, payWay, payType))
                .type(PayType.valueOf(payType))
                .apiCode(apiCode)
                .pg(PG.KAKAOPAY)
                .payStatus(PaymentStatus.PROGRESSING)
                .build();
    }
}
