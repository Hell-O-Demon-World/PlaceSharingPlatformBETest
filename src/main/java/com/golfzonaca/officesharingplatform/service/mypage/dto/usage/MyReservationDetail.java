package com.golfzonaca.officesharingplatform.service.mypage.dto.usage;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyReservationDetail {
    private String placeName;
    private String roomType;
    private String resCompletedDateTime;
    private String resStartDateTime;
    private String resEndDateTime;
    private String usageState;
    private String isAvailableReview;
}
