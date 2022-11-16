package com.golfzonaca.officesharingplatform.web.reservation.form;

import lombok.Data;

@Data
public class SelectedDateTimeForm {
    private String selectedType;
    private Integer year;
    private Integer month;
    private Integer day;
}
