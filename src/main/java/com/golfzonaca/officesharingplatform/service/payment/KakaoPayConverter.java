package com.golfzonaca.officesharingplatform.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalRequest;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelRequest;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReadyRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public interface KakaoPayConverter {

    HttpHeaders makeHttpHeader(HttpHeaders httpHeaders);

    KakaoPayReadyRequest makeRequestBodyForReady(Reservation reservation, String payWay, String payType);

    KakaoPayApprovalRequest makeRequestBodyForApprove(Reservation reservation, String pgToken);

    KakaoPayCancelRequest makeRequestBodyForCancel(Reservation reservation, Payment paymnet);

    MultiValueMap<String, String> multiValueMapConverter(ObjectMapper objectMapper, Object object);
}
