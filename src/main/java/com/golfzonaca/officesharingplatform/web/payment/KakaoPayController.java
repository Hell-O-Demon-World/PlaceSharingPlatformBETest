package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.service.payment.KakaoPayService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class KakaoPayController {

    @Setter
    @Autowired
    private KakaoPayService kakaoPayService;

    @GetMapping("/kakaoPay")
    public void kakaoPayGet() {
    }

    @PostMapping("/kakaoPay")
    public String kakaoPay() {
        return "redirect:" + kakaoPayService.kakaoPayReady();
    }

    @GetMapping("/kakaoPaySuccess")
    public void kakaoPaySuccess(@RequestParam("pg_token") String pg_token, Model model) {
        model.addAttribute("info", kakaoPayService.kakaoPayInfo(pg_token));
    }
}
