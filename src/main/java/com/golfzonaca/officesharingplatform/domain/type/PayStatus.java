package com.golfzonaca.officesharingplatform.domain.type;

public enum PayStatus {
    PREPAYMENT("선결제"), POSTPAYMENT("현장결제");
    private final String description;

    PayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
