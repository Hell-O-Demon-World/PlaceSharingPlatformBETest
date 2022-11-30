package com.golfzonaca.officesharingplatform.domain.payment;

import lombok.Data;

@Data
public class ApprovedCancelAmount {

    private Integer total; // 이번 요청으로 취소된 전체 금액
    private Integer tax_free; // 이번 요청으로 취소된 비과세 금액
    private Integer vat; // 이번 요청으로 취소된 부가세 금액
    private Integer point; // 이번 요청으로 취소된 포인트 금액
    private Integer discount; // 이번 요청으로 취소된 할인 금액
    private Integer green_deposit; // 컵 보증금
}
