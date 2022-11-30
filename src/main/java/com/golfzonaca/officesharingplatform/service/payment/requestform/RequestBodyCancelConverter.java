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
public class RequestBodyCancelConverter {

    private String cid; // 가맹점 코드
    private String tid; // 결제 고유번호
    private Integer cancelAmount; //취소 금액
    private Integer cancelTaxFreeAmount; // 취소 비과세 금액
//    private Integer cancelVatAmount; // 취소 부가세 금액
//    private Integer cancelAvailableAmount; // 취소 가능 금액
}
