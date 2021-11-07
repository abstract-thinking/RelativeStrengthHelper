package com.example.model;

public class Enterprise {

    private final String name;
    private final String symbol;

    public Enterprise(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
