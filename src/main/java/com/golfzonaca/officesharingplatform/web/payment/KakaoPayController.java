package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApproval;
import com.golfzonaca.officesharingplatform.service.payment.KakaoPayService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@RestController
public class KakaoPayController {

    @Setter
    @Autowired
    private KakaoPayService kakaoPayService;

    public KakaoPayController(KakaoPayService kakaoPayService) {
        this.kakaoPayService = kakaoPayService;
    }

    @GetMapping("/kakaoPay")
    public void kakaoPayGet() {
    }

    @PostMapping("/kakaoPay")
    public String kakaoPay(@RequestBody Map<String, Long> reservationInfo) {
        Long reservationId = reservationInfo.get("reservationId");
        return "redirect:" + kakaoPayService.kakaoPayReady(reservationId);
    }

    @GetMapping("/{reservationId}/kakaoPaySuccess")
    public KakaoPayApproval kakaoPaySuccess(@PathVariable long reservationId, @RequestParam("pg_token") String pg_token, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("pg_token", pg_token);
        redirectAttributes.addAttribute("reservationId", reservationId);
        return kakaoPayService.kakaoPayInfo(reservationId, pg_token);
    }
}
