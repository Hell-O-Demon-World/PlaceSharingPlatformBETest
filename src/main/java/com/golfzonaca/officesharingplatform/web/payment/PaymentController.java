package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.web.payment.dto.PaymentInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaymentController {

    @PostMapping("/payment")
//    ResponseEntity<?> paymentRequest(@RequestBody PaymentInfo paymentInfo) {
    RestTemplate paymentRequest(@RequestBody PaymentInfo paymentInfo) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("reservationId", String.valueOf(paymentInfo.getReservationId()));
        body.add("payWay", paymentInfo.getPayWay());
        body.add("payType", paymentInfo.getPayType());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                "http://localhost:8080/kakaoPay",
                HttpMethod.POST,
                entity,
                String.class
        );
        return restTemplate;

      /*  if (paymentInfo.getPaymentMethod().equals("KAKAO")) {
            httpHeaders.setLocation(URI.create("/kakaoPay"));
            httpHeaders.setAccessControlRequestMethod(HttpMethod.POST);
        } else if (paymentInfo.getPaymentMethod().equals("NICEPAY")) {
            httpHeaders.setLocation(URI.create("/nicepay"));
        } else {
            throw new NoSuchElementException("지원하지 않는 결제 수단입니다.");
        }
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(httpHeaders).body(paymentInfo);*/
    }
}
