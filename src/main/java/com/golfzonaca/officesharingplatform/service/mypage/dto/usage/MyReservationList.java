package com.golfzonaca.officesharingplatform.service.mypage.dto.usage;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public class MyReservationList {
    private String productType;
    private String placeName;
    private LocalDate reservationCompletedDate;
    private LocalTime reservationCompletedTime;
    private LocalDate reservationStartDate;
    private LocalTime reservationStartTime;
    private LocalDate reservationEndDate;
    private LocalTime reservationEndTime;
    private String usageStatus;
    private String ratingStatus;

}
