package com.golfzonaca.officesharingplatform.web.formatter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class TimeFormatter {
    public static LocalDate toLocalDate(String day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-M-d");
        return LocalDate.parse(day, formatter);
    }

    public static String toDayOfTheWeek(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
    }


    public static LocalTime toLocalTime(String num) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H");
        return LocalTime.parse(num, formatter);
    }
}
