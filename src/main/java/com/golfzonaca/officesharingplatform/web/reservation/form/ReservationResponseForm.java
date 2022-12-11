package com.golfzonaca.officesharingplatform.web.reservation.form;

import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReservationResponseForm {
    private Long reservationId;
    private RoomType roomType;
    private String placeName;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
    private Long price;
    private Long totalMileage;
}
