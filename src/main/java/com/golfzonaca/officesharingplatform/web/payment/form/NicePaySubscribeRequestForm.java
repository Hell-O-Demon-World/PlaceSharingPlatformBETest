package com.golfzonaca.officesharingplatform.web.payment.form;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NicePaySubscribeRequestForm {

    private long reservationId;
    private String customerUid;
    private String customerId;
    private int checking_amount;
    private String cardNumber;
    private String expiry;
    private String birth;
    private String pwd_2digit;
    private String cvc;
    private String pg;
}
