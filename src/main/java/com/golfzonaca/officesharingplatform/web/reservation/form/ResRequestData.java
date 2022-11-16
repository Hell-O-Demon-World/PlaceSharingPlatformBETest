package com.golfzonaca.officesharingplatform.web.reservation.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResRequestData {

    private String selectedType;
    private String year;
    private String month;
    private String day;
    private String startTime;
    private String endTime;
    private String accessToken;
}
