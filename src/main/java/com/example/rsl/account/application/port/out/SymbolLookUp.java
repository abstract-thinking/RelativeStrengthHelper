package com.example.rsl.account.application.port.out;

import com.example.rsl.account.domain.Enterprise;

public interface SymbolLookUp {

    Enterprise lookUp(String symbol);
}
