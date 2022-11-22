package com.golfzonaca.officesharingplatform.web.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterData {
    private String day;
    private String startTime;
    private String endTime;
    private String city;
    private String subCity;
    private String type;
}