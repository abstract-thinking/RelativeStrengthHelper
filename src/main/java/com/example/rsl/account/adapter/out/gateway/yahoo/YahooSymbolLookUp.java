package com.example.rsl.account.adapter.out.gateway.yahoo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.rsl.account.adapter.out.gateway.yahoo.api.YahooLookupResult;
import com.example.rsl.account.application.port.out.SymbolLookUp;
import com.example.rsl.account.domain.Enterprise;

@Primary
@Service
class YahooSymbolLookUp implements SymbolLookUp {

    private static final String URL = "https://query1.finance.yahoo.com/v1/finance/lookup?query={symbol}&type=equity&count=1"
            + "&start=0";

    public Enterprise lookUp(String symbol) {
        var result = new RestTemplate().getForEntity(URL, YahooLookupResult.class, symbol).getBody();
        if (result == null) {
            return new Enterprise("Name not found", symbol);
        }

        // TODO: Ist there a better solution to get the name
        return new Enterprise(result.finance.result.get(0).documents.get(0).shortName, symbol);
    }
}
