package com.golfzonaca.officesharingplatform.service.payment.kakaopay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.CompanyId;
import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalRequest;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelRequest;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReadyRequest;
import com.golfzonaca.officesharingplatform.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoPayConverterImpl implements KakaoPayConverter {

    private final KakaoPayUtility kakaoPayUtility;
    public static String payWay;
    public static String payType;


    @Override
    public HttpHeaders makeHttpHeader(HttpHeaders httpHeaders) {
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "KakaoAK a8e95d70e35d823f1171ddaa015b53c4");
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        return httpHeaders;
    }

    @Override
    public KakaoPayReadyRequest makeRequestBodyForReady(Reservation reservation, String payWay, String payType) {
        this.payWay = payWay;
        this.payType = payType;

        Integer totalAmount = kakaoPayUtility.calculateTotalAmount(reservation, payWay, payType);

        return KakaoPayReadyRequest.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .partnerOrderId(String.valueOf(reservation.getId()))
                .partnerUserId(String.valueOf(reservation.getUser().getId()))
                .itemName(String.valueOf(reservation.getRoom().getRoomKind().getRoomType()))
                .quantity(1)
                .totalAmount(totalAmount)
                .taxFreeAmount(kakaoPayUtility.calculateTaxFreeAmount(totalAmount))
                .vatAmount(kakaoPayUtility.calculateVatAmount(totalAmount))
                .approvalUrl("http://localhost:8080/payment/" + reservation.getId() + "/kakaoPayApprove")
                .cancelUrl("http://localhost:8080/payment/kakaoPayCancel")
                .failUrl("http://localhost:8080/kakaoPaySuccessFail")
                .build();
    }

    @Override
    public KakaoPayApprovalRequest makeRequestBodyForApprove(Reservation reservation, String pgToken) {

//        Integer totalAmount = kakaoPayUtility.calculateTotalAmount(reservation, this.payWay, this.payType);

        return KakaoPayApprovalRequest.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .tid(PaymentService.kakaoPayReadyResponse.getTid())
                .partnerOrderId(String.valueOf(reservation.getId()))
                .partnerUserId(String.valueOf(reservation.getUser().getId()))
                .pgToken(pgToken)
                .build();
    }

    @Override
    // TODO 1 : 1시간 이내 결제된 PAYMENT 모두 변경됨 -> 추가 조건 필요
    public KakaoPayCancelRequest makeRequestBodyForCancel(Reservation reservation, Payment payment) {

        int cancelAmount = (int) payment.getPrice();

        return KakaoPayCancelRequest.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .tid(payment.getApiCode())
                .cancelAmount(cancelAmount)
                .cancelTaxFreeAmount(kakaoPayUtility.calculateTaxFreeAmount(cancelAmount))
                .cancelVatAmount(kakaoPayUtility.calculateVatAmount(cancelAmount))
                .build();
    }

    @Override
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
