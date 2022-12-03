package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReadyResponse;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

public interface KakaoPayService {

    /**
     * 카카오페이 API로 결제 준비 요청
     *
     * @param reservationId, payWay
     * @return KakaoPayReadyResponse
     */
    String kakaoPayReadyRequest(long reservationId, String payWay, String payType);

    /**
     * 카카오페이 API로 결제 승인요청
     *
     * @param reservationId, pgToken
     * @return KakaoPayApprovalResponse
     */
    KakaoPayApprovalResponse kakaoPayApprovalRequest(long reservationId, String pgToken);

    /**
     * 카카오페이 API로 결제 취소 요청
     *
     * @param reservationId
     * @return KakaoPayCancelResponse
     */
    KakaoPayCancelResponse kakaoPayCancelRequest(long reservationId);

    KakaoPayReadyResponse sendKakaoPayReadyRequest(String host, HttpEntity<MultiValueMap<String, String>> body);

    KakaoPayApprovalResponse sendKakaoPayApprovalRequest(String host, HttpEntity<MultiValueMap<String, String>> body);

    KakaoPayCancelResponse sendKakaoPayCancelRequest(String host, HttpEntity<MultiValueMap<String, String>> body);

    void savePayment(Reservation reservation);

    void saveMileage(User user, long point);
}
