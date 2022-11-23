package com.golfzonaca.officesharingplatform.service.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {

    private String name;
    private String description;
    private String address;
    private String mainInfo;
    private String openDays;
    private String openTime;
    private String endTime;
    private LinkedHashMap<String, List<String>> roomInfo;
    private LinkedHashMap<String, List<String>> picture;
    private LinkedHashMap<String, List<String>> rating;
    private LinkedHashMap<String, List<String>> comment;
    private LinkedHashMap<String, List<String>> inquiry;
}
