package com.golfzonaca.officesharingplatform.domain.payment;

import lombok.Data;

@Data
public class CancelAvailableAmount {

    private Integer total; // 전체 취소 가능 금액
    private Integer tax_free; // 취소 가능한 비과세 금액
    private Integer vat; // 취소 가능한 부가세 금액
    private Integer point; // 취소 가능한 포인트 금액
    private Integer discount; // 취소 가능한 할인 금액
    private Integer green_deposit; // 컵 보증금

}
