package com.golfzonaca.officesharingplatform.domain.type.dateformat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DateFormat {
    private Integer year;
    private Integer month;
    private Integer day;

    public void toEntity(Integer year, Integer month, Integer day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
