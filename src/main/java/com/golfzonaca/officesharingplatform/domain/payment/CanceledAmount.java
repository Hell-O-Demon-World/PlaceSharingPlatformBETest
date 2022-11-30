package com.golfzonaca.officesharingplatform.domain.payment;

import lombok.Data;

@Data
public class CanceledAmount {

    private Integer total; // 취소된 전체 누적금액
    private Integer tax_free; // 취소된 비과세 누적 금액
    private Integer vat; // 취소된 부가세 누적 금액
    private Integer point; // 취소된 포인트 누적 금액
    private Integer discount; // 취소된 할인 누적 금액 
    private Integer green_deposit; // 컵 보증금
}
