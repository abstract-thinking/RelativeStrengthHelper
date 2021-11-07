package com.example.model;

import java.time.LocalDate;

public record HistoricalQuote(LocalDate date, Double close) {

    public LocalDate getDate() {
        return date;
    }

    public Double getClose() {
        return close;
    }

}
