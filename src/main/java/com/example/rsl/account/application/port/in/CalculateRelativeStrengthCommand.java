package com.example.rsl.account.application.port.in;

import lombok.NonNull;
import lombok.Value;

@Value
public class CalculateRelativeStrengthCommand {

    @NonNull
    String symbol;

    int period;
}
