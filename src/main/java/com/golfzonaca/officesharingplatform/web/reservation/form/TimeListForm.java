package com.golfzonaca.officesharingplatform.web.reservation.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class TimeListForm {
    private List<Integer> timeList;
    private Map<String, String> errorMap;
}
