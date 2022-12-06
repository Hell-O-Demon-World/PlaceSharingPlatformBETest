package com.golfzonaca.officesharingplatform.service.payment.kakaopay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.payment.*;
import com.golfzonaca.officesharingplatform.domain.type.PG;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.domain.type.PayWay;
import com.golfzonaca.officesharingplatform.repository.mileage.MileageRepository;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com/";

    public static KakaoPayReadyResponse kakaoPayReadyResponse;

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final MileageRepository mileageRepository;

    public String kakaoPayReadyRequest(long reservationId, String payWay, String payType, long payMileage) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        HttpHeaders httpHeaders = kakaoPayUtility.makeHttpHeader();


        Reservation reservation = reservationRepository.findById(reservationId);
        Payment payment = getPayment(reservation, payWay, payType, payMileage, "");
        paymentRepository.save(payment);

        KakaoPayReadyRequest kakaoPayReadyRequest = kakaoPayUtility.makeRequestBodyForReady(payment);


        HttpEntity<MultiValueMap<String, String>> requestReadyEntity = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), kakaoPayReadyRequest), httpHeaders);
        kakaoPayReadyResponse = sendKakaoPayReadyRequest(HOST, requestReadyEntity);
        payment.updateApiCode(kakaoPayReadyResponse.getTid());


        return kakaoPayReadyResponse.getNextRedirectPcUrl();
    }


    public KakaoPayApprovalResponse kakaoPayApprovalRequest(long paymentId, String pgToken) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        HttpHeaders httpHeaders = kakaoPayUtility.makeHttpHeader();

        Payment findPayment = paymentRepository.findById(paymentId);

        KakaoPayApprovalRequest body = kakaoPayUtility.makeRequestBodyForApprove(findPayment, pgToken);

        HttpEntity<MultiValueMap<String, String>> requestApprovalEntity = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), body), httpHeaders);
        KakaoPayApprovalResponse kakaoPayApprovalResponse = sendKakaoPayApprovalRequest(HOST, requestApprovalEntity);

        findPayment.updatePayStatus(true);

        return kakaoPayApprovalResponse;
    }

    public KakaoPayCancelResponse kakaoPayCancelRequest(long reservationId) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        HttpHeaders httpHeaders = kakaoPayUtility.makeHttpHeader();


        Reservation reservation = reservationRepository.findById(reservationId);
        Payment findPayment = checkAvailablePaymentInHour(reservationId);

        KakaoPayCancelRequest kakaoPayCancelRequest = kakaoPayUtility.makeRequestBodyForCancel(reservation, findPayment);

        HttpEntity<MultiValueMap<String, String>> requestCancelEntity = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), kakaoPayCancelRequest), httpHeaders);
        KakaoPayCancelResponse kakaoPayCancelResponse = sendKakaoPayCancelRequest(HOST, requestCancelEntity);

        // todo : update 구문에 조건 더 필요 id + kakaoPayCancelResponse.getTid()
        // detail : reservationId 말고 tid 만 이용해서 삭제해도 될 것 같다 ~~
        updatePaymentStatus(reservationId, kakaoPayCancelResponse.getTid());

        return kakaoPayCancelResponse;
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

    //todo : 마일리지 업데이트 필요
    public void saveMileage(User user, long point) {
        // mileageRepository 에서 userId로 Mileage 찾음
        // 찾은 Mileage에서 .getPoint 하고 그것을 파라미터로 온 point와 더함
        // 그리고 mileageRepository에서 update 할건데
        //  where 조건은 userId

//        long originPoint = mileageRepository.findByID()
//        Mileage mileage = new Mileage(user, point);
//
    }

    public Payment checkAvailablePaymentInHour(long reservationId) {

        List<Payment> payments = paymentRepository.findByReservationId(reservationId);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        Payment findPayment = null;

        for (Payment payment : payments) {
            if (payment.getPayDate().equals(currentDate)) {
                if ((Duration.between(currentTime, payment.getPayTime())).getSeconds() < 3600) {
                    findPayment = payment;
                }
            }
        }

        return findPayment;
    }

    // todo : update 구문에 조건 더 필요 id + ...
    // payment 에서는 tid 로 검색해서 payStatus update 하면 됨
    // reservation에서는 쿼리가 잘 동작 안하는 것 같으니 확인 필요
    public void updatePaymentStatus(long reservationId, String tid) {

        for (Payment payment : paymentRepository.findByReservationId(reservationId)) {
            payment.updatePayStatus(false);
        }
        reservationRepository.findById(reservationId).updateStatus(false);
    }

    private Payment getPayment(Reservation reservation, String payWay, String payType, long payMileage, String apiCode) {

        KakaoPayUtility kakaoPayUtility = new KakaoPayUtility();

        Integer totalAmount = kakaoPayUtility.calculateTotalAmount(reservation, payWay, payType);

        return Payment.builder()
                .reservation(reservation)
                .payDate(LocalDate.now())
                .payTime(LocalTime.now())
                .price(totalAmount)
                .payMileage(payMileage)
                .payWay(PayWay.valueOf(payWay))
                .savedMileage(kakaoPayUtility.calculateMileage(totalAmount))
                .type(PayType.valueOf(payType))
                .apiCode(apiCode)
                .pg(PG.KAKAOPAY)
                .payStatus(false) //TODO : DB 바뀜에 따라 바뀌어 질 예정
                .build();
    }
}
