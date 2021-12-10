package com.example.rsl.account.application.port.in;

import lombok.NonNull;

public record CalculateRelativeStrengthCommand(@NonNull String symbol, int period) {

}
