package com.example.gateway.tradier.dto;

import com.example.gateway.marketstack.dto.Quotes;
import lombok.Data;

import java.util.List;

@Data
public class TimeSeries {
    List<Quotes> day;
}
