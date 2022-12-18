package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.payment.iamport.IamportService;
import com.golfzonaca.officesharingplatform.web.payment.dto.CancelInfo;
import com.golfzonaca.officesharingplatform.web.payment.form.NicePayRequestForm;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class IamportController {

    private final IamportService iamportService;
    private final PaymentValidation paymentValidation;

    @PostMapping("/nicepay")
    public void nicePay(@TokenUserId Long userId, @RequestBody NicePayRequestForm nicePayRequestForm, HttpServletResponse response) throws IamportResponseException, IOException {
        String url = iamportService.requestNicePay(userId, nicePayRequestForm);
        response.sendRedirect(url);
    }

    @PostMapping("/nicepaycancel")
    public void nicepayCancel(@TokenUserId Long userId, @RequestBody CancelInfo cancelInfo, HttpServletResponse response) throws IamportResponseException, IOException {
        long reservationId = cancelInfo.getReservationId();
        paymentValidation.cancelRequest(reservationId);
        String url = iamportService.nicePayCancel(userId, reservationId);
        iamportService.cancelByReservationAndUserId(reservationId, userId);
        response.sendRedirect(url);
    }

}
