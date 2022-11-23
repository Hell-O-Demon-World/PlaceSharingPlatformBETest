package com.golfzonaca.officesharingplatform.service.payment;


import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalForm;

public interface KakaoPayService {

    String kakaoPayReady(long reservationId);

    KakaoPayApprovalForm kakaoPayInfo(long reservationId, String pg_token);

    KakaoPayApprovalForm save(KakaoPayApprovalForm kakaoPayApprovalForm);
}
