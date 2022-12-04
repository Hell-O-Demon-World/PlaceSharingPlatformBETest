package com.golfzonaca.officesharingplatform.web.reservation.dto.response;

import com.golfzonaca.officesharingplatform.domain.type.dateformat.DateFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationResponseData {
    private String productType;
    private boolean state;
    private DateFormat date;
}
