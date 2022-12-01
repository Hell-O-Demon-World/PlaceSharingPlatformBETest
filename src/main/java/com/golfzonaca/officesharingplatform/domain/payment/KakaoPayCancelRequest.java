package com.golfzonaca.officesharingplatform.domain.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoPayCancelRequest {

    private String cid; // 가맹점 코드
    private String cid_secret; // 가맹점 코드 인증키
    private String tid; // 결제 고유번호
    private Integer cancelAmount; //취소 금액
    private Integer cancelTaxAmount; // 취소 비과세 금액
    private Integer cancelVatAmount; // 취소 부가세 금액
    private Integer cancelAvailableAmount; // 취소 가능 금액
    private String payload; // 해당 요청에 대해 저장하고 싶은 값 최대 200자
}
