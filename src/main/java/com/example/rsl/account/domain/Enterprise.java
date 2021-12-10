package com.example.rsl.account.domain;

import java.util.Arrays;

import com.example.rsl.account.adapter.in.web.Exchange;

public record Enterprise(String name, String symbol) {

    public boolean isIndex() {
        return Arrays.stream(Exchange.values()).anyMatch(exchange -> exchange.name().equals(name));
    }
}
