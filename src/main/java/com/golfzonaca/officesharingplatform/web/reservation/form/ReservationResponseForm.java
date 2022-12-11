package com.golfzonaca.officesharingplatform.web.reservation.form;

import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReservationResponseForm {
    private Long reservationId;
    private RoomType roomType;
    private String placeName;
    private String reservationStartDate;
    private String reservationStartTime;
    private String reservationEndDate;
    private String reservationEndTime;
    private Integer price;
    private Long totalMileage;
}
