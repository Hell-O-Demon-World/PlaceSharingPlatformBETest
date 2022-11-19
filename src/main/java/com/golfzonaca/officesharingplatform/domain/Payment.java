package com.golfzonaca.officesharingplatform.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Payment {
    private long id;
    private long userId;
    private long roomId;
    private LocalDate payDate;
    private long payPrice;
    private String payStatus;
    private long payMileage;
    private String payType;
    private String payApiCode;

    public Payment(long userId, long roomId, LocalDate payDate, long payPrice, String payStatus, long payMileage, String payType, String payApiCode) {
        this.userId = userId;
        this.roomId = roomId;
        this.payDate = payDate;
        this.payPrice = payPrice;
        this.payStatus = payStatus;
        this.payMileage = payMileage;
        this.payType = payType;
        this.payApiCode = payApiCode;
    }
}
