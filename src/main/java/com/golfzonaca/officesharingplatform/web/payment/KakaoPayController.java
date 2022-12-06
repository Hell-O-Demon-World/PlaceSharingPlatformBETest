package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelResponse;
import com.golfzonaca.officesharingplatform.service.payment.kakaopay.KakaoPayService;
import com.golfzonaca.officesharingplatform.web.payment.dto.PaymentInfo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;


//    @GetMapping("/kakaoPay")
//    public void kakaoPayGet() {
//        log.info("kakaoPayGet");
//    }

    @PostMapping("/kakaoPay")
    public String kakaoPayReady(@RequestBody PaymentInfo paymentInfo) {
        log.info("kakaoPayReady");
        Long reservationId = paymentInfo.getReservationId();
        long payMileage = paymentInfo.getPayMileage();
        String payWay = paymentInfo.getPayWay();
        String payType = paymentInfo.getPayType();
        return "redirect:" + kakaoPayService.kakaoPayReadyRequest(reservationId, payWay, payType, payMileage);
    }

    @GetMapping("/{paymentId}/kakaoPayApprove")
    public KakaoPayApprovalResponse kakaoPayApprove(@PathVariable long paymentId, @RequestParam("pg_token") String pg_token, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("pg_token", pg_token);
        redirectAttributes.addAttribute("paymentId", paymentId);
        return kakaoPayService.kakaoPayApprovalRequest(paymentId, pg_token);
    }

    @PostMapping("/kakaoPayCancel")
    public KakaoPayCancelResponse kakaoPayCancel(@RequestBody Map<String, Long> reservationInfo) {
        Long reservationId = reservationInfo.get("reservationId");
        return kakaoPayService.kakaoPayCancelRequest(reservationId);
    }
}
