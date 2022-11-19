package com.golfzonaca.officesharingplatform.service.payment;


import com.golfzonaca.officesharingplatform.web.payment.form.KakaoPayApprovalForm;
import com.golfzonaca.officesharingplatform.web.payment.form.KakaoPayReady;

public interface KakaoPayService {

    String kakaoPayReady();

    KakaoPayApprovalForm kakaoPayInfo(String pg_token);

}
