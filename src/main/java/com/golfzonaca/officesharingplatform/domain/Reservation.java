package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@RequiredArgsConstructor
@Data
public class Reservation {
    private long id;
    private long placeId;
    private long userId;
    private long roomId;
    private long roomKindId;
    private LocalDate resStartDate;
    private LocalTime resStartTime;
    private LocalDate resEndDate;
    private LocalTime resEndTime;

    public Reservation(long placeId, long userId, long roomId, long roomKindId, LocalDate resStartDate, LocalTime resStartTime, LocalDate resEndDate, LocalTime resEndTime) {
        this.placeId = placeId;
        this.userId = userId;
        this.roomId = roomId;
        this.roomKindId = roomKindId;
        this.resStartDate = resStartDate;
        this.resStartTime = resStartTime;
        this.resEndDate = resEndDate;
        this.resEndTime = resEndTime;
    }
}