package com.golfzonaca.officesharingplatform.service.payment;


import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelRequest;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelResponse;

public interface KakaoPayService {

    String kakaoPayReady(long reservationId, String payWay);

    KakaoPayApprovalResponse kakaoPayInfo(long reservationId, String pg_token);

    KakaoPayApprovalResponse save(KakaoPayApprovalResponse kakaoPayApprovalResponse);

    KakaoPayCancelResponse cancel(long reservationId);
}
