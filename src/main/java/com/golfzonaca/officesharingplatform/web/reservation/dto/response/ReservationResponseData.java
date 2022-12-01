package com.golfzonaca.officesharingplatform.web.reservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationResponseData {
    private String productType;
    private boolean state;
    private LocalDate date;
}
