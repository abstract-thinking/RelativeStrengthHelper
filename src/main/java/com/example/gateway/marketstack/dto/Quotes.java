package com.example.gateway.marketstack.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public record Quotes(String date, double open, double high, double low, double close, double volume) {

    public LocalDate localDate() {
        final DateTimeFormatter isoFormatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .appendOffset("+HHMM", "+0000")
                .toFormatter();

        return LocalDate.parse(date, isoFormatter);
    }
}
