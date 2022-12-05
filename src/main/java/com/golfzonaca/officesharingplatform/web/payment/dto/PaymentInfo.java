package com.golfzonaca.officesharingplatform.web.payment.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentInfo {
    
    private long reservationId;
    private String payWay;
    private String payType;
//    private String cardNumber;
//    private String expiry;
//    private String birth;
//    private String pwd_2digit;
//    private String paymentMethod;
}
