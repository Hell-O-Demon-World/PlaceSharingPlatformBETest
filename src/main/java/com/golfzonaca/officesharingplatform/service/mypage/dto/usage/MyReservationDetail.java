package com.golfzonaca.officesharingplatform.service.mypage.dto.usage;

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

    public MyReservationDetail(String placeName, String roomType, String resCompletedDate, String resCompletedTime, String resStartDate, String resStartTime, String resEndDate, String resEndTime, String usageState, Long totalPrice, Double savedMileage) {
        this.placeName = placeName;
        this.roomType = roomType;
        this.resCompletedDate = resCompletedDate;
        this.resCompletedTime = resCompletedTime;
        this.resStartDate = resStartDate;
        this.resStartTime = resStartTime;
        this.resEndDate = resEndDate;
        this.resEndTime = resEndTime;
        this.usageState = usageState;
        this.totalPrice = totalPrice;
        this.savedMileage = savedMileage;
    }

    public void addIsAvailableReview(Boolean isAvailableReview) {
        this.isAvailableReview = isAvailableReview;
    }
}
