package com.golfzonaca.officesharingplatform.web.formatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    public static LocalDate toLocalDate(String day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-M-d");
        LocalDate date = LocalDate.parse(day, formatter);
        return date;
    }
}
