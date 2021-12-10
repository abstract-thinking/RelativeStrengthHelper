package com.example.rsl.account.application.port.in;

import com.example.rsl.account.adapter.in.web.Exchange;

import lombok.NonNull;
import lombok.Value;

@Value
public class CalculateRelativeStrengthsCommand {
    @NonNull
    Exchange exchange;

    int period;
}
