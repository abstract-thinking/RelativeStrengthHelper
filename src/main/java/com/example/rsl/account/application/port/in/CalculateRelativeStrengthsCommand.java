package com.example.rsl.account.application.port.in;

import com.example.rsl.account.adapter.in.web.Exchange;

import lombok.NonNull;

public record CalculateRelativeStrengthsCommand(@NonNull Exchange exchange, int period) {
}
