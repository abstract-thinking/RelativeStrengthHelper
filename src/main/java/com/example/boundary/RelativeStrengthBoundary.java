package com.example.boundary;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.control.RelativeStrengthCollector;
import com.example.boundary.api.Exchange;
import com.example.boundary.api.RelativeStrengthResult;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@RequestMapping("/rs")
@RestController
public class RelativeStrengthBoundary {

    private RelativeStrengthCollector collector;

    @GetMapping("/symbols/{symbol}")
    public RelativeStrengthResult relativeStrength(@PathVariable("symbol") String symbol) {
        return collector.collect(symbol, 135);
    }

    @GetMapping("/exchanges/{exchange}")
    public List<RelativeStrengthResult> relativeStrength(@PathVariable("exchange") Exchange exchange) {
        return collector.collect(exchange, 135);
    }
}
