package com.golfzonaca.officesharingplatform.service.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReadyRequest;
import com.golfzonaca.officesharingplatform.domain.type.PG;
import com.golfzonaca.officesharingplatform.domain.type.PayWay;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Slf4j
@Service
public class KakaoPayUtilityImpl implements KakaoPayUtility {


    @Override
    public void accumulationMileage(User user, long payPrice) {
        long addMileage = (long) (payPrice * 0.05); // 마일리지를 사용했으면 마일리지 사용한 금액은 빼고 사용자가 지불한 금액에 대해서 %적용 해서 마일리지 계산해주기
        user.getMileage().addPoint(user.getMileage().getPoint(), addMileage);
    }

    @Override
    public long calculateDeposit(long payPrice) {
        return (long) (payPrice * 0.2);
    }

    @Override
    public LocalDate toLocalDate(LocalDateTime localDateTime) {
        return LocalDate.from(localDateTime);
    }

    @Override
    public LocalTime toLocalTime(LocalDateTime localDateTime) {
        return LocalTime.from(localDateTime);
    }

    @Override
    public String calculatePayPrice(Reservation reservation, RoomKind roomKind) {
        return String.valueOf((reservation.getResEndTime().getHour() - reservation.getResStartTime().getHour()) * roomKind.getPrice());
    }

    @Override
    public String taxFreeAmount(String calculatePayPrice) {
        return String.valueOf((Integer.parseInt(calculatePayPrice) * 10 / 11));
    }

    @Override
    public String vatAmount(String calculatePayPrice) {
        return String.valueOf((Integer.parseInt(calculatePayPrice) / 11));
    }

    @Override
    public void savePaymentInfo(PaymentRepository paymentRepository, Reservation reservation, User user, Room room, KakaoPayApprovalResponse kakaoPayApprovalResponse) {

        LocalDateTime localDateTime = kakaoPayApprovalResponse.getApproved_at();
        LocalDate payDate = this.toLocalDate(localDateTime);
        LocalTime payTime = this.toLocalTime(localDateTime);
        long payPrice = kakaoPayApprovalResponse.getAmount().getTotal(); // 총 금액을 가져옴 카카오 api 승인에서 내려주는
        long payMileage = 0L; //추후 변경예정
        PayWay payWay = checkPayStatus(reservation);
//        PayStatus payStatus = PayStatus.PREPAYMENT;
        long savedMileage = kakaoPayApprovalResponse.getAmount().getPoint();

        PayType payType = PayType.FULLPAYMENT;

        if (payWay.equals(PayWay.PREPAYMENT)) { // 선결제일 때
            this.accumulationMileage(user, payPrice);
        } else { // 현장결제일 때
            if (!kakaoPayApprovalResponse.getItem_name().contains("OFFICE")) {
                payType = PayType.DEPOSIT;
                payPrice = this.calculateDeposit(payPrice);
            }
        }
        String payApiCode = kakaoPayApprovalResponse.getTid();

        Payment payment = new Payment(reservation, payDate, payTime, payPrice, payMileage, payWay, savedMileage, payType, payApiCode, PG.KAKAOPAY, true);

        paymentRepository.save(payment);
    }

    @Override
    public PayWay checkPayStatus(Reservation reservation) {

        PayWay payWay;

        if (reservation == null) {
            payWay = PayWay.PREPAYMENT;
        } else {
            payWay = PayWay.POSTPAYMENT;
        }
        return payWay;
    }

    @Override
    public MultiValueMap<String, String> multiValueMapConverter(ObjectMapper objectMapper, Object dto) { // (2)
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<>() {
            }); // (3)
            params.setAll(map); // (4)

            return params;
        } catch (Exception e) {
            log.error("Url Parameter 변환중 오류가 발생했습니다. requestDto={}", dto, e);
            throw new IllegalStateException("Url Parameter 변환중 오류가 발생했습니다.");
        }
    }

    @Override
    public HttpHeaders makeHttpHeader(HttpHeaders httpHeaders) {
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "KakaoAK " + "a8e95d70e35d823f1171ddaa015b53c4");
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        return httpHeaders;
    }

    @Override
    public KakaoPayApprovalResponse toEntity(String host, HttpEntity<MultiValueMap<String, String>> body) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.postForObject(new URI(host + "/v1/payment/approve"), body, KakaoPayApprovalResponse.class);
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String kakaoPayReadyToEntity(String host, HttpEntity<MultiValueMap<String, String>> body) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayReadyRequest kakaoPayReadyRequest = restTemplate.postForObject(new URI(host + "/v1/payment/ready"), body, KakaoPayReadyRequest.class);
            return kakaoPayReadyRequest.getNext_redirect_pc_url();
        } catch (RestClientException | URISyntaxException e) {
            log.error(e.toString());
        }
        return "/pay";
    }

    @Override
    public KakaoPayReadyRequest kakaoPayReadyRequestApprove(String host, HttpEntity<MultiValueMap<String, String>> body) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayReadyRequest kakaoPayReadyRequest = restTemplate.postForObject(new URI(host + "/v1/payment/ready"), body, KakaoPayReadyRequest.class);
            return kakaoPayReadyRequest;
        } catch (RestClientException | URISyntaxException e) {
            log.error(e.toString());
        }
        return null;
    }

}
