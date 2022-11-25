package com.golfzonaca.officesharingplatform.service.payment;


import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApproval;

public interface KakaoPayService {

    String kakaoPayReady(long reservationId);

    KakaoPayApproval kakaoPayInfo(long reservationId, String pg_token);

    KakaoPayApproval save(KakaoPayApproval kakaoPayApproval);
}
