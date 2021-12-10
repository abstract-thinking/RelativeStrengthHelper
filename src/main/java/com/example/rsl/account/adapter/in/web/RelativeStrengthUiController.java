package com.example.rsl.account.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.rsl.account.application.port.in.CalculateRelativeStrengthUseCase;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthsCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/rsl")
@Controller
public class RelativeStrengthUiController {

    private final CalculateRelativeStrengthUseCase calculateRelativeStrengthUseCase;

    @GetMapping("/exchanges/{exchange}")
    public String showRsl(Model model, @PathVariable Exchange exchange) {
        log.info("RSL invoked");

        final RelativeStrengthResultWrapper result =
                calculateRelativeStrengthUseCase.calculateRelativeStrengths(
                        new CalculateRelativeStrengthsCommand(exchange, 135));
        model.addAttribute("wrapper", result);

        log.info("RSL done");

        return "rsl";
    }
}