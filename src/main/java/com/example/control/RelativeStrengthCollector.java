package com.example.control;

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
        Enterprise enterprise = enterpriseFetcher.lookUp(symbol);

        RelativeStrengthCalculator calculator = new RelativeStrengthCalculator(period);

        List<HistoricalQuote> historicalQuotes = historicalQuotesFetcher.fetch(enterprise, period);
        return new RelativeStrengthResult(enterprise, calculator.calculate(historicalQuotes));
    }

    public List<RelativeStrengthResult> collect(Exchange exchange, int period) {
        List<Enterprise> enterprises = enterpriseFetcher.fetch(exchange);

        RelativeStrengthCalculator calculator = new RelativeStrengthCalculator(period);
        List<RelativeStrengthResult> result = new ArrayList<>();

        for (Enterprise enterprise : enterprises) {
            List<HistoricalQuote> historicalQuotes = historicalQuotesFetcher.fetch(enterprise, period);
            result.add(new RelativeStrengthResult(enterprise, calculator.calculate(historicalQuotes)));
        }

        return result;
    }
}
