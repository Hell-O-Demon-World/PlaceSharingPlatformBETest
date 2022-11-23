package com.golfzonaca.officesharingplatform.web.reservation.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResRequestData {

    @NotBlank
    private String selectedType;
    @NotBlank
    private String date;
    @NotBlank
    private String startTime;
    @NotBlank
    private String endTime;
}