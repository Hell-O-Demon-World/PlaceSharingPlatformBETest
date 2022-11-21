package com.golfzonaca.officesharingplatform.web.payment.form;

import java.time.LocalDateTime;

public class KakaoPaySuccessInfo {

    private LocalDateTime approvedAt;
    private Long partnerOrderId;
    private String itemName;
    private int quantity;
    private int amountTotal;
    private String paymentMethodType;
}
