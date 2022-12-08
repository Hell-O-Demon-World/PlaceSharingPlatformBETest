package com.golfzonaca.officesharingplatform.service.mypage.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyPaymentDetail {
    private String payDateTime;
    private long payPrice;
    private long payMileage;
    private String payWay;
    private long savedMileage;
    private String payType;
    private String pg;
}
