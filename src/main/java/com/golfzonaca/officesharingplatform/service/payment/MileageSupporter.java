package com.golfzonaca.officesharingplatform.service.payment;


import java.time.LocalDateTime;

public class MileageSupporter {
    private static final int MAX_EXPIRED_YEARS = 5;
    private static final String REASON_PAYMENT = "선결제 적립";

    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }
    public static LocalDateTime expiredDateTime() {
        return LocalDateTime.now().plusYears(MAX_EXPIRED_YEARS);
    }
    public static String paymentReason() {
        return REASON_PAYMENT;
    }
}
