package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.service.payment.iamport.IamPortService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/payment")
public class IamPortController {

    private final IamPortService iamPortService;


    @PostMapping("/iamportPay")
    public IamportResponse<Payment> iamPort() throws IamportResponseException, IOException {
        IamportClient iamportClient = new IamportClient("3356213051155874", "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf");
        OnetimePaymentData onetimeData = new OnetimePaymentData("1q2w3e4r", new BigDecimal(1), new CardInfo("1111-2222-3333-4444", "2022-01", "990101", "00"));
//        onetimeData.setPg("jtnet");
        onetimeData.setPg("nice");
        return iamportClient.onetimePayment(onetimeData);
    }

    @PostMapping("/iamportCancel")
    public IamportResponse<Payment> iamPortCancel() throws IamportResponseException, IOException {
        log.info("iamportCancel() = {}", iamPortCancel());
        IamportClient iamportClient = new IamportClient("3356213051155874", "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf");
        CancelData cancelData = new CancelData("imp03070546", true, new BigDecimal(2000));
        return iamportClient.cancelPaymentByImpUid(cancelData);
    }

    @PostMapping("/nicepay")
    public IamportResponse<Payment> nicePay(@RequestBody Map<String, String> nicePayInfo) throws IamportResponseException, IOException {
        String cardNumber = nicePayInfo.get("card_number");
        String expiry = nicePayInfo.get("expiry");
        String birth = nicePayInfo.get("birth");
        String pwd2digit = nicePayInfo.get("pwd_2digit");

        CardInfo cardInfo = new CardInfo(cardNumber, expiry, birth, pwd2digit);
        return iamPortService.nicePay(cardInfo);
    }

    @PostMapping("/nicePaycancel")
    public IamportResponse<Payment> nicePayCancel(@RequestBody Map<String, String> nicePayInfo) throws IamportResponseException, IOException {
        return iamPortService.nicePayCancel();
    }
}
