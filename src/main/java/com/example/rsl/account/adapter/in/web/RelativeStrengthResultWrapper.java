package com.example.rsl.account.adapter.in.web;

import java.time.LocalDate;
import java.util.List;


public record RelativeStrengthResultWrapper(String exchange, LocalDate date, List<RelativeStrengthResult> relativeStrengthResults) {
}
