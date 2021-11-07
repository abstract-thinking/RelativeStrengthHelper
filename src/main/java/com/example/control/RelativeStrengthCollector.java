package com.example.control;

import com.example.gateway.EnterpriseFetcher;
import com.example.gateway.marketstack.HistoricalQuotesFetcher;
import com.example.model.Enterprise;
import com.example.model.Exchange;
import com.example.model.HistoricalQuote;
import com.example.model.RelativeStrength;
import com.example.model.RelativeStrengthResult;

import java.util.ArrayList;
import java.util.List;

import static com.example.model.Exchange.HDAX;

public class RelativeStrengthCollector {

    private final EnterpriseFetcher enterpriseFetcher = new EnterpriseFetcher();
    private final HistoricalQuotesFetcher historicalQuotesFetcher = new HistoricalQuotesFetcher();

    public static void main(String[] args) {
        List<RelativeStrengthResult> result = new RelativeStrengthCollector().collect(HDAX, 135);

        result.size();
    }

    public List<RelativeStrengthResult> collect(Exchange exchange, int period) {
        List<Enterprise> enterprises = enterpriseFetcher.fetch(exchange);

        RelativeStrengthCalculator calculator = new RelativeStrengthCalculator(period);
        List<RelativeStrengthResult> result = new ArrayList<>();

        for (Enterprise enterprise : enterprises) {
            List<HistoricalQuote> historicalQuotes = historicalQuotesFetcher.fetch(enterprise, period);
            RelativeStrength relativeStrength = calculator.calculate(historicalQuotes);

            result.add(new RelativeStrengthResult(enterprise, relativeStrength));
        }

        return result;
    }
}
