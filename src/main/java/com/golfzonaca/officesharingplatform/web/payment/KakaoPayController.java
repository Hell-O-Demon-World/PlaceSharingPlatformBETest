package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.web.payment.dto.kakaopay.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.web.payment.dto.kakaopay.KakaoPayCancelResponse;
import com.golfzonaca.officesharingplatform.service.payment.kakaopay.KakaoPayService;
import com.golfzonaca.officesharingplatform.web.payment.dto.CancelInfo;
import com.golfzonaca.officesharingplatform.web.payment.dto.PaymentInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakaopay")
    public String kakaoPayReady(@TokenUserId Long userId,@RequestBody PaymentInfo paymentInfo) {
        log.info("kakaoPayReady");
        long reservationId = paymentInfo.getReservationId();
        long payMileage = paymentInfo.getPayMileage();
        String payWay = paymentInfo.getPayWay();
        String payType = paymentInfo.getPayType();
        return kakaoPayService.kakaoPayReadyRequest(userId, reservationId, payWay, payType, payMileage);
    }

    @GetMapping("/{paymentId}/kakaopayapprove")
    public void kakaoPayApprove(@PathVariable long paymentId, @RequestParam("pg_token") String pgToken, HttpServletResponse response) throws IOException {
        kakaoPayService.kakaoPayApprovalRequest(paymentId, pgToken);
        response.sendRedirect("http://localhost:3000/mypage/usage");
    }

    @PostMapping("/kakaopaycancel")
    public List<KakaoPayCancelResponse> kakaoPayCancel(@TokenUserId Long userId, @RequestBody CancelInfo cancelInfo) {
        Long reservationId = cancelInfo.getReservationId();
        return kakaoPayService.kakaoPayCancel(userId, reservationId);
    }
}
