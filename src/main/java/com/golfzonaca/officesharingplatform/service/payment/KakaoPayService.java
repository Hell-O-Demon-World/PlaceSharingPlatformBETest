package com.golfzonaca.officesharingplatform.service.payment;


import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalForm;

public interface KakaoPayService {

    String kakaoPayReady(Long reservationId);

    KakaoPayApprovalForm kakaoPayInfo(String pg_tokenpg_token);

}
