package com.example.control;

import com.example.model.RelativeStrength;
import com.example.model.HistoricalQuote;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelativeStrengthCalculator {


//    public static void main(String[] args) throws IOException, CsvException {
//        List<HistoricalQuote> historicalQuotes = new CsvReaderClient().read();
//
//        RelativeStrength relativeStrength = calculateRelativeStrength(historicalQuotes);
//        System.err.println(relativeStrength);
//
//        List<RelativeStrength> historicalRelativeStrength = calculateHistoricalRelativeStrength(historicalQuotes);
//    }

    private final int period;

    public RelativeStrengthCalculator(int period) {
        this.period = period;
    }

    public List<RelativeStrength> calculateHistoricalRelativeStrength(List<HistoricalQuote> historicalQuotes) {
        List<RelativeStrength> historicalRelativeStrengths = new ArrayList<>();

        SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage(period);
        for (int i = 1; i <= historicalQuotes.size(); ++i) {
            simpleMovingAverage.addData(historicalQuotes.get(i - 1).getClose());
            if (i >= period) {
                LocalDate date = historicalQuotes.get(i - period).getDate();
                double relativeStrength = historicalQuotes.get(i - period).getClose() / simpleMovingAverage.getMean();
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
            simpleMovingAverage.addData(historicalQuotes.get(i - 1).getClose());
            if (i >= period) {
                LocalDate date = historicalQuotes.get(i - period).getDate();
                double relativeStrength = historicalQuotes.get(i - period).getClose() / simpleMovingAverage.getMean();
                return createRelativeStrength(date, relativeStrength);
            }
        }

        throw new IllegalStateException("Too less entries: " + historicalQuotes.size());
    }
}
