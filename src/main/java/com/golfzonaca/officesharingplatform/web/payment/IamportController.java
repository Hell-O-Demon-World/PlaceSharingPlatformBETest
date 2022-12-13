package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.payment.iamport.IamportService;
import com.golfzonaca.officesharingplatform.web.payment.dto.CancelInfo;
import com.golfzonaca.officesharingplatform.web.payment.form.NicePayRequestForm;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class IamportController {

    private final IamportService iamportService;

    @PostMapping("/nicepay")
    public IamportResponse<Payment> nicePay(@TokenUserId Long userId, @RequestBody NicePayRequestForm nicePayRequestForm) throws IamportResponseException, IOException {
        return iamportService.requestNicePay(userId, nicePayRequestForm);
    }

    @PostMapping("/nicpaycancel")
    public List<IamportResponse<Payment>> nicepayCancel(@TokenUserId Long userId, @RequestBody CancelInfo cancelInfo) throws IamportResponseException, IOException {
        long reservationId = cancelInfo.getReservationId();
        return iamportService.nicePayCancel(userId, reservationId);
    }
}
