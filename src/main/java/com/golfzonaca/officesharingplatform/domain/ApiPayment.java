package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.domain.type.PayType;

public class ApiPayment {
    private long id;
    private long trxId;
    private long userId;
    private PayType trxType;
    private String trxApiCode;
}
