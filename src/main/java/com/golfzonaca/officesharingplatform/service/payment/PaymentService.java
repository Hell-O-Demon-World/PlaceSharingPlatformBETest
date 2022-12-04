package com.golfzonaca.officesharingplatform.service.payment;

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
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements KakaoPayService, IamPortService {

    private static final String HOST = "https://kapi.kakao.com/";
    private static final HttpHeaders httpheaders = new HttpHeaders();

    protected static KakaoPayReadyResponse kakaoPayReadyResponse;

    private final KakaoPayConverter kakaoPayConverter;
    private final KakaoPayUtility kakaoPayUtility;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final MileageRepository mileageRepository;

    @Override
    public String kakaoPayReadyRequest(long reservationId, String payWay, String payType) {

        Reservation reservation = reservationRepository.findById(reservationId);

        kakaoPayConverter.makeHttpHeader(httpheaders);
        KakaoPayReadyRequest kakaoPayReadyRequest = kakaoPayConverter.makeRequestBodyForReady(reservation, payWay, payType);

        HttpEntity<MultiValueMap<String, String>> requestReadyEntity = new HttpEntity<>(kakaoPayConverter.multiValueMapConverter(new ObjectMapper(), kakaoPayReadyRequest), httpheaders);
        kakaoPayReadyResponse = sendKakaoPayReadyRequest(HOST, requestReadyEntity);

        return kakaoPayReadyResponse.getNextRedirectPcUrl();
    }

    @Override
    public KakaoPayApprovalResponse kakaoPayApprovalRequest(long reservationId, String pgToken) {

        Reservation reservation = reservationRepository.findById(reservationId);
        KakaoPayApprovalRequest kakaoPayApprovalRequest = kakaoPayConverter.makeRequestBodyForApprove(reservation, pgToken);

        HttpEntity<MultiValueMap<String, String>> requestApprovalEntity = new HttpEntity<>(kakaoPayConverter.multiValueMapConverter(new ObjectMapper(), kakaoPayApprovalRequest), httpheaders);
        KakaoPayApprovalResponse kakaoPayApprovalResponse = sendKakaoPayApprovalRequest(HOST, requestApprovalEntity);

        savePayment(reservation);

        return kakaoPayApprovalResponse;
    }

    @Override
    public KakaoPayCancelResponse kakaoPayCancelRequest(long reservationId) {

        checkHttpHeader();

        Reservation reservation = reservationRepository.findById(reservationId);
        Payment findPayment = checkAvailablePaymentInHour(reservationId);

        KakaoPayCancelRequest kakaoPayCancelRequest = kakaoPayConverter.makeRequestBodyForCancel(reservation, findPayment);

        HttpEntity<MultiValueMap<String, String>> requestCancelEntity = new HttpEntity<>(kakaoPayConverter.multiValueMapConverter(new ObjectMapper(), kakaoPayCancelRequest), httpheaders);
        KakaoPayCancelResponse kakaoPayCancelResponse = sendKakaoPayCancelRequest(HOST, requestCancelEntity);

        // todo : update 구문에 조건 더 필요 id + kakaoPayCancelResponse.getTid()
        // detail : reservationId 말고 tid 만 이용해서 삭제해도 될 것 같다 ~~
        updatePaymentStatus(reservationId, kakaoPayCancelResponse.getTid());

        return kakaoPayCancelResponse;
    }

    @Override
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

    @Override
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

    @Override
    public void savePayment(Reservation reservation) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        Integer totalAmount = kakaoPayUtility.calculateTotalAmount(reservation, KakaoPayConverterImpl.payWay, KakaoPayConverterImpl.payType);

        long point = kakaoPayUtility.calculateMileage(totalAmount);
        Payment payment = new Payment(reservation, currentDate, currentTime, totalAmount, 0, PayWay.valueOf(KakaoPayConverterImpl.payWay), point, PayType.valueOf(KakaoPayConverterImpl.payType), kakaoPayReadyResponse.getTid(), PG.KAKAOPAY, true);

        paymentRepository.save(payment);
        saveMileage(reservation.getUser(), point);
    }

    @Override
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

    public void checkHttpHeader() {
        if (httpheaders.isEmpty()) {
            kakaoPayConverter.makeHttpHeader(httpheaders);
        }
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


    @Override
    public IamportResponse<com.siot.IamportRestClient.response.Payment> nicePay(CardInfo cardInfo) throws IamportResponseException, IOException {
        IamportClient iamportClient = new IamportClient("3356213051155874", "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf");
        OnetimePaymentData onetimeData = new OnetimePaymentData("1q2w3e4r", new BigDecimal(1000), cardInfo);
//        onetimeData.setPg("jtnet");
        onetimeData.setPg("nice");
        return iamportClient.onetimePayment(onetimeData);
    }

    @Override
    public IamportResponse<com.siot.IamportRestClient.response.Payment> nicePayCancel() throws IamportResponseException, IOException {
        IamportClient iamportClient = new IamportClient("3356213051155874", "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf");
        CancelData cancelData = new CancelData("1q2w3e4r", false, new BigDecimal(1000));
        cancelData.setTax_free(new BigDecimal(0));
        cancelData.setChecksum(null);
        cancelData.setReason("test");

        return iamportClient.cancelPaymentByImpUid(cancelData);
    }
}
