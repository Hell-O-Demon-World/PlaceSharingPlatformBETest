package com.golfzonaca.officesharingplatform.service.payment.kakaopay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalRequest;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelRequest;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReadyRequest;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.domain.type.PayWay;
import com.golfzonaca.officesharingplatform.web.payment.dto.PaymentInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class KakaoPayUtility {

    public Integer calculateTotalAmount(Reservation reservation, String payWay, String payType, long payMileage) {

        int totalAmount;
        int payPrice;

        if (reservation.getRoom().getRoomKind().getRoomType().contains("OFFICE")) {
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

    public HttpHeaders makeHttpHeader() {

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.HOST, "https://kapi.kakao.com/");
        headers.add(HttpHeaders.AUTHORIZATION, "KakaoAK a8e95d70e35d823f1171ddaa015b53c4");
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        return headers;
    }


    public KakaoPayReadyRequest makeRequestBodyForReady(Payment payment) {

        Integer totalAmount = (int) payment.getPrice();

        Reservation reservation = payment.getReservation();

        return KakaoPayReadyRequest.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .partnerOrderId(String.valueOf(reservation.getId()))
                .partnerUserId(String.valueOf(reservation.getUser().getId()))
                .itemName(String.valueOf(reservation.getRoom().getRoomKind().getRoomType()))
                .quantity(1)
                .totalAmount(totalAmount)
                .taxFreeAmount(calculateTaxFreeAmount(totalAmount))
                .vatAmount(calculateVatAmount(totalAmount))
                .approvalUrl("http://localhost:8080/payment/" + payment.getId() + "/kakaopayapprove")
                .cancelUrl("http://localhost:8080/payment/kakaopaycancel")
                .failUrl("http://localhost:8080/kakaopaysuccessfail")
                .build();
    }

    public KakaoPayApprovalRequest makeRequestBodyForApprove(Payment payment, String pgToken) {


        return KakaoPayApprovalRequest.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .tid(payment.getApiCode())
                .partnerOrderId(String.valueOf(payment.getReservation().getId()))
                .partnerUserId(String.valueOf(payment.getReservation().getUser().getId()))
                .pgToken(pgToken)
                .build();
    }

    public KakaoPayCancelRequest makeRequestBodyForCancel(Refund refund) {

        int cancelAmount = (int) refund.getRefundPrice();

        return KakaoPayCancelRequest.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .tid(refund.getPayment().getApiCode())
                .cancelAmount(cancelAmount)
                .cancelTaxFreeAmount(calculateTaxFreeAmount(cancelAmount))
                .cancelVatAmount(calculateVatAmount(cancelAmount))
                .build();
    }

    public MultiValueMap<String, String> multiValueMapConverter(ObjectMapper objectMapper, Object object) {

        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map = objectMapper.convertValue(object, new TypeReference<>() {
            });
            params.setAll(map);

            return params;

        } catch (Exception e) {
            log.error("MultiValueMapConverter ConvertingError = {}", object, e);

            throw new IllegalStateException("MultiValueMapConverter ConvertingError");
        }
    }
}