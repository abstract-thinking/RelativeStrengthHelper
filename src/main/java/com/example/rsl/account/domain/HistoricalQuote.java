package com.example.rsl.account.domain;

import java.time.LocalDate;

public record HistoricalQuote(LocalDate date, Double close) { }
