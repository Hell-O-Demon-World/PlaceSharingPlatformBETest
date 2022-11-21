package com.golfzonaca.officesharingplatform.web.reservation.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResRequestData {

    private String selectedType;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String accessToken;
}
