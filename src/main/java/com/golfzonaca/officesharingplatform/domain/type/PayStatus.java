package com.golfzonaca.officesharingplatform.domain.type;

public enum PayStatus {
    PrePay("선결제"), OnsitePay("현장 결제");
    private final String description;

    PayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
