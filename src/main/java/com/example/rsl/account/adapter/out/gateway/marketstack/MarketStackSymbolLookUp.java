package com.example.rsl.account.adapter.out.gateway.marketstack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.rsl.account.application.port.out.SymbolLookUp;
import com.example.rsl.account.domain.Enterprise;

@Service
class MarketStackSymbolLookUp implements SymbolLookUp {

    private static final String URL = "http://api.marketstack.com/v1/tickers/{symbol}?access_key={accessKey}";

    @Value("${access.key}")
    private String accessKey;

    public Enterprise lookUp(String symbol) {
        record Description(String symbol, String name) {}

        var description = new RestTemplate().getForEntity(URL, Description.class, symbol, accessKey).getBody();
        if (description == null) {
            return new Enterprise("Name not found", symbol);
        }

        return new Enterprise(description.name(), description.symbol());
    }

}
