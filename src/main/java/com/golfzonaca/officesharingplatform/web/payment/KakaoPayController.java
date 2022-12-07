package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelResponse;
import com.golfzonaca.officesharingplatform.service.payment.kakaopay.KakaoPayService;
import com.golfzonaca.officesharingplatform.web.payment.dto.CancelInfo;
import com.golfzonaca.officesharingplatform.web.payment.dto.PaymentInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakaopay")
    public String kakaoPayReady(@RequestBody PaymentInfo paymentInfo) {
        log.info("kakaoPayReady");
        long reservationId = paymentInfo.getReservationId();
        long payMileage = paymentInfo.getPayMileage();
        String payWay = paymentInfo.getPayWay();
        String payType = paymentInfo.getPayType();
        return kakaoPayService.kakaoPayReadyRequest(reservationId, payWay, payType, payMileage);
    }

    @GetMapping("/{paymentId}/kakaopayapprove")
    public KakaoPayApprovalResponse kakaoPayApprove(@PathVariable long paymentId, @RequestParam("pg_token") String pgToken) {
        return kakaoPayService.kakaoPayApprovalRequest(paymentId, pgToken);
    }

    @PostMapping("/kakaopaycancel")
    public KakaoPayCancelResponse kakaoPayCancel(@RequestBody CancelInfo cancelInfo) {
        Long reservationId = cancelInfo.getReservationId();
        return kakaoPayService.kakaoPayCancelRequest(reservationId);
    }
}
