package com.golfzonaca.officesharingplatform.service.mypage.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class MyReservationList {
    private String productType;
    private String placeName;
    private LocalDateTime reservationCompletedDateTime;
    private LocalDateTime reservationStartDateTime;
    private LocalDateTime reservationEndDateTime;
    private String usageStatus;
    private String ratingStatus;

}
