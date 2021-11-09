package com.example.control;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.model.HistoricalQuote;
import com.example.model.RelativeStrength;

public record RelativeStrengthCalculator(int period) {

    public List<RelativeStrength> calculateHistoricalRelativeStrength(List<HistoricalQuote> historicalQuotes) {
        List<RelativeStrength> historicalRelativeStrengths = new ArrayList<>();

        SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage(period);
        for (int i = 1; i <= historicalQuotes.size(); ++i) {
            simpleMovingAverage.addData(historicalQuotes.get(i - 1).close());
            if (i >= period) {
                LocalDate date = historicalQuotes.get(i - period).date();
                double relativeStrength = historicalQuotes.get(i - period).close() / simpleMovingAverage.getMean();
                historicalRelativeStrengths.add(createRelativeStrength(date, relativeStrength));
            }
        }

        return historicalRelativeStrengths;
    }

    private RelativeStrength createRelativeStrength(LocalDate localDate, double rsl) {
        return new RelativeStrength(localDate, rsl);
    }

    public RelativeStrength calculate(List<HistoricalQuote> historicalQuotes) {
        SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage(period);
        for (int i = 1; i <= historicalQuotes.size(); ++i) {
            simpleMovingAverage.addData(historicalQuotes.get(i - 1).close());
            if (i >= period) {
                LocalDate date = historicalQuotes.get(i - period).date();
                double relativeStrength = historicalQuotes.get(i - period).close() / simpleMovingAverage.getMean();
                return createRelativeStrength(date, relativeStrength);
            }
        }

        throw new IllegalStateException("Too less entries: " + historicalQuotes.size());
    }
}
