package com.golfzonaca.officesharingplatform.service.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApproval;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReady;
import com.golfzonaca.officesharingplatform.domain.type.PayStatus;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
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
        long addMileage = (long) (payPrice * 0.05);
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
        return String.valueOf((int) (Integer.parseInt(calculatePayPrice) * 0.9));
    }

    @Override
    public void savePaymentInfo(PaymentRepository paymentRepository, Reservation reservation, User user, Room room, KakaoPayApproval kakaoPayApproval) {

        LocalDateTime localDateTime = kakaoPayApproval.getApproved_at();
        LocalDate payDate = this.toLocalDate(localDateTime);
        LocalTime payTime = this.toLocalTime(localDateTime);
        long payPrice = kakaoPayApproval.getAmount().getTotal();
        PayStatus payStatus = checkPayStatus(reservation);
        long payMileage = kakaoPayApproval.getAmount().getPoint();

        PayType payType = PayType.FULLPAYMENT;

        if (payStatus.equals(PayStatus.PREPAYMENT)) { // 선결제일 때
            this.accumulationMileage(user, payPrice);
        } else { // 현장결제일 때
            if (!kakaoPayApproval.getItem_name().contains("OFFICE")) {
                payType = PayType.DEPOSIT;
                payPrice = this.calculateDeposit(payPrice);
            }
        }
        String payApiCode = kakaoPayApproval.getTid();

        Payment payment = new Payment(user, room, payDate, payTime, payPrice, payStatus, payMileage, payType, payApiCode);

        paymentRepository.save(payment);
    }

    @Override
    public PayStatus checkPayStatus(Reservation reservation) {
        PayStatus payStatus;

        if (reservation == null) {
            payStatus = PayStatus.PREPAYMENT;
        } else {
            payStatus = PayStatus.POSTPAYMENT;
        }
        return payStatus;
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
    public KakaoPayApproval toEntity(String host, HttpEntity<MultiValueMap<String, String>> body) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.postForObject(new URI(host + "/v1/payment/approve"), body, KakaoPayApproval.class);
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String kakaoPayReadyToEntity(String host, HttpEntity<MultiValueMap<String, String>> body) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayReady kakaoPayReady = restTemplate.postForObject(new URI(host + "/v1/payment/ready"), body, KakaoPayReady.class);
            return kakaoPayReady.getNext_redirect_pc_url();
        } catch (RestClientException | URISyntaxException e) {
            log.error(e.toString());
        }
        return "/pay";
    }

    @Override
    public KakaoPayReady kakaoPayGetTid(String host, HttpEntity<MultiValueMap<String, String>> body){
        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayReady kakaoPayReady = restTemplate.postForObject(new URI(host + "/v1/payment/ready"), body, KakaoPayReady.class);
            return kakaoPayReady;
        } catch (RestClientException | URISyntaxException e) {
            log.error(e.toString());
        }
        return null;
    }

}
