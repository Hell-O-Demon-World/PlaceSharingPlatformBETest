package com.golfzonaca.officesharingplatform.web.reservation.form;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResRequestData {

    private String selectedType;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
