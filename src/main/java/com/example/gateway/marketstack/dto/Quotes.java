package com.example.gateway.marketstack.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Quotes {
    LocalDate date;
    double open;
    double high;
    double low;
    double close;
    double volume;
}
