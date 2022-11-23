package com.golfzonaca.officesharingplatform.service.payment.requestform;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.golfzonaca.officesharingplatform.domain.payment.CompanyId;
import com.golfzonaca.officesharingplatform.domain.payment.kakaopay.KakaoPayReady;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestBodyApproveConverter {

    private String cid; // 가맹점 코드
    private String tid; // 결제 고유 번호
    private String partnerOrderId; // 가맹점 주문 번호
    private String partnerUserId; // 가맹점 회원 ID
    private String pgToken; // 결제 승인 요청을 인증하는 토큰
    private String totalAmount; // 상품 총액

    public RequestBodyApproveConverter toEntity(String cid, String tid, String partnerOrderId, String partnerUserId, String pgToken, String totalAmount) {
        return RequestBodyApproveConverter.builder()
                .cid(cid)
                .tid(tid)
                .partnerOrderId(partnerOrderId)
                .partnerUserId(partnerUserId)
                .pgToken(pgToken)
                .totalAmount(totalAmount)
                .build();
    }
}
