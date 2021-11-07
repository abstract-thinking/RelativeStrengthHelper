package com.example;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.indicator.response.sma.SMAResponse;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.SeriesType;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import org.junit.jupiter.api.Test;

public class MeTest {

    @Test
    public void testMe() {
        Config cfg = Config.builder()
                .key("ET8YAFYCD18N03QD")
                .timeOut(10)
                .build();

        AlphaVantage.api().init(cfg);

        SMAResponse smaResponse = AlphaVantage.api().indicator()
                .sma()
                .forSymbol("IBM")
                .seriesType(SeriesType.CLOSE)
                .timePeriod(135)
                .interval(Interval.DAILY)
                .fetchSync();

        TimeSeriesResponse timeSeriesResponse = AlphaVantage.api().timeSeries()
                .daily()
                .forSymbol("IBM")
                .fetchSync();

        double rsl = timeSeriesResponse.getStockUnits().get(0).getClose() / smaResponse.getIndicatorUnits().get(0).getValue();

        System.err.println(rsl);
    }


}
