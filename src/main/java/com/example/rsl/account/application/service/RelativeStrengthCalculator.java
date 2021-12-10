package com.example.rsl.account.application.service;

import java.time.LocalDate;
import java.util.List;

import com.example.rsl.account.domain.Enterprise;
import com.example.rsl.account.domain.HistoricalQuote;
import com.example.rsl.account.domain.RelativeStrength;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RelativeStrengthCalculator {

    public static RelativeStrength calculate(List<HistoricalQuote> historicalQuotes, int period, Enterprise enterprise) {
       final SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage(period);
         for (int i = 1; i <= historicalQuotes.size(); ++i) {
            simpleMovingAverage.addData(historicalQuotes.get(i - 1).close());
            if (i >= period) {
                LocalDate date = historicalQuotes.get(i - period).date();
                double relativeStrength = historicalQuotes.get(i - period).close() / simpleMovingAverage.getMean();
                return new RelativeStrength(date, relativeStrength);
            }
        }

        throw new IllegalStateException("Too less entries: " + historicalQuotes.size() + " for " + enterprise.name());
    }
}
