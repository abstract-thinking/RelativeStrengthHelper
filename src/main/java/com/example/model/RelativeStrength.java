package com.example.model;

import java.time.LocalDate;

public record RelativeStrength(LocalDate date, Double rsl) {

    public LocalDate getDate() {
        return date;
    }

    public Double getRsl() {
        return rsl;
    }

}
