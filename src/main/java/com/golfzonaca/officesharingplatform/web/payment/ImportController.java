package com.golfzonaca.officesharingplatform.web.payment;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImportController {

    private static final String restKey = "3356213051155874";
    private static final String secretKey = "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf";

    private final IamportClient iamportClient = new IamportClient(restKey, secretKey);

    @GetMapping("/iamport")
    public void paymentByImpUid() throws IamportResponseException, IOException {
        log.info("import 나와라");

        String merchant_id = "ORD_" + new Date().getTime();

        PrepareData prepareData = new PrepareData(merchant_id, new BigDecimal(5000));
        IamportResponse<Prepare> result1 = iamportClient.postPrepare(prepareData);
        log.info(result1.toString());

    }


    @PostMapping("/iamport")
    public IamportResponse<Prepare> paymentByImpUid(@RequestBody Map<String, String> restKeyInfo) throws IOException, IamportResponseException {

        String restKey = "3356213051155874";
        String secretKey = "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf";

        IamportClient iamportClient = new IamportClient(restKey, secretKey);

//        String restKey = restKeyInfo.get("merchant_id2");
        log.error("paymentByImpUid 진입");
//        log.error("impUid={}", restKey);

        //        iamportClient.getAuth();
/*
        iamportClient.getBillingCustomer("any");

        iamportClient.postBillingCustomer("any", new BillingCustomerData("any", "111-222-333-444", "2022-12", "950904"));


        String merchant_id = "ORD_" + new Date().getTime();
        String merchant_id2 = "b09LVzhuTGZVaEY1WmJoQnZzdXpRdz09";

//        PrepareData prepareData = new PrepareData(merchant_id, new BigDecimal(5000));
//        IamportResponse<Prepare> result1 = iamportClient.postPrepare(prepareData);
//        log.info(result1.toString());

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchant_id, new BigDecimal(5000), new CardInfo("111-222-333-444", "2022-12", "950904", "09"));
        onetimePaymentData.setPg("inicis");
        // 결제를 진행하기 위해 아래와 같이 client 객체의 requestPayment 메서드로 요청합니다.
        IamportResponse<Payment> result2 = iamportClient.onetimePayment(onetimePaymentData);
        log.info("result2 = {}", result2.getResponse().toString());*/


        String merchant_id = "ord_" + new Date().getTime();
        IamportResponse<Prepare> preparePost = iamportClient.postPrepare(new PrepareData(merchant_id, new BigDecimal("5000")));
        log.info("preparePost={}", preparePost);

        IamportResponse<Prepare> prepareGet = iamportClient.getPrepare(merchant_id);
        log.info("prepareGet={}", prepareGet);


        IamportResponse<Payment> response = iamportClient.paymentByImpUid("imp03070546");
        log.info("response", response);
        return null;
    }
}
