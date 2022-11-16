package com.golfzonaca.officesharingplatform.domain.type;

public enum PayType {
    Deposit("보증금"), Balance("잔금") , FullPayment("전체 결제");
    private final String description;
    PayType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
