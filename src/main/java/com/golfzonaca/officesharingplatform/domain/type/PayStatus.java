package com.golfzonaca.officesharingplatform.domain.type;

public enum PayStatus {
<<<<<<< HEAD
    PrePay("선결제"), OnsitePay("현장 결제");
=======
    PREPAYMENT("선결제"), POSTPAYMENT("현장결제");
>>>>>>> payment
    private final String description;

    PayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
