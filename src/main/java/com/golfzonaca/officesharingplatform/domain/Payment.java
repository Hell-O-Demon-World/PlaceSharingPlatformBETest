package com.golfzonaca.officesharingplatform.domain;

import java.util.Date;

public class Payment {
    private long id;
    private long userId;
    private long mileageId;
    private Date trxDate;
    private int companyId;
    private int roomId;
    private int trxPrice;
    private boolean trxStatus;
    private int trxMileage;
}
