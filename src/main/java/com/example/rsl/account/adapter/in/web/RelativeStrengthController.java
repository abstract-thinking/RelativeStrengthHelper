package com.example.rsl.account.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rsl.account.application.port.in.CalculateRelativeStrengthCommand;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthUseCase;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthsCommand;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@RequestMapping("/rs")
@RestController
public class RelativeStrengthController {

    private final CalculateRelativeStrengthUseCase calculateRelativeStrengthUseCase;

    @GetMapping("/symbols/{symbol}")
    public RelativeStrengthResult relativeStrength(@PathVariable("symbol") String symbol) {
        return calculateRelativeStrengthUseCase.calculateRelativeStrength(new CalculateRelativeStrengthCommand(symbol, 135));
    }

    @GetMapping("/exchanges/{exchange}")
    public RelativeStrengthResultWrapper relativeStrengths(@PathVariable("exchange") Exchange exchange) {
        return calculateRelativeStrengthUseCase.calculateRelativeStrengths(new CalculateRelativeStrengthsCommand(exchange, 135));
    }
}
