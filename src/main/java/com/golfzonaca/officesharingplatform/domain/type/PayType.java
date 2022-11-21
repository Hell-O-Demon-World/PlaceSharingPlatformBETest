package com.golfzonaca.officesharingplatform.domain.type;

public enum PayType {
    DEPOSIT("보증금"), BALANCE("잔금") , FULLPAYMENT("전체 결제");
    private final String description;
    PayType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
