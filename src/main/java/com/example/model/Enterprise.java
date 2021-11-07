package com.example.model;

public class Enterprise {

    private final String name;
    private final String isin;

    public Enterprise(String name, String isin) {
        this.name = name;
        this.isin = isin;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return isin;
    }
}
