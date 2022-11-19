package com.golfzonaca.officesharingplatform.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class Payment {
    private long id;
    private long userId;
    private long roomId;
    private LocalDate payDate;
    private LocalTime payTime;
    private long payPrice;
    private String payStatus;
    private long payMileage;
    private String payType;
    private String payApiCode;

    public Payment(long userId, long roomId, LocalDate payDate, LocalTime payTime, long payPrice, String payStatus, long payMileage, String payType, String payApiCode) {
        this.userId = userId;
        this.roomId = roomId;
        this.payDate = payDate;
        this.payTime = payTime;
        this.payPrice = payPrice;
        this.payStatus = payStatus;
        this.payMileage = payMileage;
        this.payType = payType;
        this.payApiCode = payApiCode;
    }
}
