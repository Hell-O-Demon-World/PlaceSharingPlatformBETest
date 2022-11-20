package com.golfzonaca.officesharingplatform.web.search.dto;

import com.golfzonaca.officesharingplatform.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseData {
    private long key;
    private String name;
    private Address address;
    private String openDays;
    private LocalTime startTime;
    private LocalTime endTime;
    private String option;


}
