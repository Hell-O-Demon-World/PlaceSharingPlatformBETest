package com.golfzonaca.officesharingplatform.service.mypage.dto.usage;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class MyReservationDetail {
    private String placeName;
    private String roomType;
    private String resCompletedDate;
    private String resCompletedTime;
    private String resStartDate;
    private String resStartTime;
    private String resEndDate;
    private String resEndTime;
    private String usageState;
    private Boolean isAvailableReview;
    private Long totalPrice;
    private Double savedMileage;
    private Boolean cancelStatus;
    private Boolean completeStatus;

    public void addIsAvailableReview(Boolean isAvailableReview) {
        this.isAvailableReview = isAvailableReview;
    }
}
