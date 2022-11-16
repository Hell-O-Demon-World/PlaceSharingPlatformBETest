package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Place {

    private long id;
    private long companyId;
    private String placeName;
    private String placeDescription;
    private String placeOpendays;
    private LocalTime placeStart;
    private LocalTime placeEnd;
    private Map<String, Boolean> placeAddinfo;
    private int addressId;
}
