package com.golfzonaca.officesharingplatform.service.mypage.dto.usage;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyPaymentDetail {
    private String payDate;
    private String payTime;
    private long payPrice;
    private long payMileage;
    private String payWay;
    private long savedMileage;
    private String payType;
    private String pg;
    private String receipt;
}
