package com.example.gateway.marketstack.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoricalResult {
    private Pagination pagination;
    private List<Quotes> data;
}
