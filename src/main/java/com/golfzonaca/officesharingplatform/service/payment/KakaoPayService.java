package com.golfzonaca.officesharingplatform.service.payment;


import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalForm;

public interface KakaoPayService {

    String kakaoPayReady();

    KakaoPayApprovalForm kakaoPayInfo(String pg_token);

}
