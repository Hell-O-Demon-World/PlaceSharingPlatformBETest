package com.golfzonaca.officesharingplatform.domain.type;

public enum RoomType {
    DESK("데스크"), MEETINGROOM("회의실"), OFFICE("사무실");
    private final String description;

    RoomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
