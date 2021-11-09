package com.example.control;

import static com.example.control.RelativeStrengthCalculator.calculate;

import com.example.gateway.EnterpriseFetcher;
import com.example.gateway.marketstack.HistoricalQuotesFetcher;
import com.example.model.Enterprise;
import com.example.boundary.api.Exchange;
import com.example.model.HistoricalQuote;
import com.example.model.RelativeStrength;
import com.example.boundary.api.RelativeStrengthResult;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RelativeStrengthCollector {

    private EnterpriseFetcher enterpriseFetcher;

    private HistoricalQuotesFetcher historicalQuotesFetcher;

    public RelativeStrengthResult collect(String symbol, int period) {
        final var enterprise = enterpriseFetcher.lookUp(symbol);

        var historicalQuotes = historicalQuotesFetcher.fetch(enterprise, period);
        return new RelativeStrengthResult(enterprise, calculate(historicalQuotes, period));
    }

    public List<RelativeStrengthResult> collect(Exchange exchange, int period) {
        var enterprises = enterpriseFetcher.fetch(exchange);

        var result = new ArrayList<RelativeStrengthResult>();
        for (var enterprise : enterprises) {
            var historicalQuotes = historicalQuotesFetcher.fetch(enterprise, period);
            result.add(new RelativeStrengthResult(enterprise, calculate(historicalQuotes, period)));
        }

        return result;
    }
}
