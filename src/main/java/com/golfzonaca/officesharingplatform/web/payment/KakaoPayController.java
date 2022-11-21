package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.service.payment.KakaoPayService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String kakaoPay(Long reservationId) {
        return "redirect:" + kakaoPayService.kakaoPayReady(reservationId);
    }

    @GetMapping("/{reservationId}/kakaoPaySuccess")
    public String kakaoPaySuccess(@PathVariable Long reservationId, @RequestParam("pg_token") String pg_token, RedirectAttributes model) {
        model.addAttribute("pg_token", pg_token);
        return "redirect:/kakaoPaySuccess";
    }

    @GetMapping("/kakaoPaySuccess")
    public void kakaoPaySuccesss(@RequestParam("pg_token") String pg_token, Model model) {
        model.addAttribute("info", kakaoPayService.kakaoPayInfo(pg_token));
    }
}
