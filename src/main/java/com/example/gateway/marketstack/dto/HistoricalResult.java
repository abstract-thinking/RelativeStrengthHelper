package com.example.gateway.marketstack.dto;

import java.util.List;

public record HistoricalResult(Pagination pagination, List<Quotes> data) {
}
