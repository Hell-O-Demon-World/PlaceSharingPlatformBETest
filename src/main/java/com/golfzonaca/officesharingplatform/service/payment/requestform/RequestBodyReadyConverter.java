package com.golfzonaca.officesharingplatform.service.payment.requestform;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestBodyReadyConverter {

    private String cid; // 가맹점 코드
    private String partnerOrderId; // 가맹점 주문 번호
    private String partnerUserId; // 가맹점 회원 ID
    private String itemName; // 상품명
    private String quantity; // 상품 수량
    private String totalAmount; // 상품 총액
    private String taxFreeAmount; // 상품 비과세 금액
    private String vatAmount; // 상품 세금
    private String approvalUrl; // 결제 성공 시 redirect url
    private String cancelUrl; // 결제 취소 시 redirect url
    private String failUrl; // 결제 실패 시 redirect url
}