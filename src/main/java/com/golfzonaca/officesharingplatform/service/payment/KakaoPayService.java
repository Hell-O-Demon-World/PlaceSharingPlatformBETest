package com.golfzonaca.officesharingplatform.service.payment;


import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalForm;

public interface KakaoPayService {

    String kakaoPayReady(Long reservationId);

    KakaoPayApprovalForm kakaoPayInfo(Long reservationId, String pg_token);

}
