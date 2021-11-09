package com.example.gateway.marketstack;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.model.Enterprise;
import com.example.model.HistoricalQuote;

@Service
public class HistoricalQuotesFetcher {

    private static final String URL = "http://api.marketstack.com/v1/eod?access_key="
            + "{access_key}&symbols={symbol}&limit={limit}&date_from={start_date}&date_to={stop_date}";

    @Value("${access.key}")
    private String accessKey;

    public List<HistoricalQuote> fetch(Enterprise enterprise, int period) {
        record Quotes(String date, double open, double high, double low, double close, double volume) {
            public LocalDate localDate() {
                return LocalDate.parse(date, createDateTimeFormatter());
            }

            private DateTimeFormatter createDateTimeFormatter() {
                return new DateTimeFormatterBuilder()
                        .append(ISO_LOCAL_DATE_TIME)
                        .appendOffset("+HHMM", "+0000")
                        .toFormatter();
            }
        }

        record Pagination( int limit, int offset, int count, int total) { }
        record HistoricalResult(Pagination pagination, List<Quotes> data) { }

        var stopDate = LocalDate.now();
        var startDate = stopDate.minusYears(1);
        var historicalResult = new RestTemplate().getForEntity(URL, HistoricalResult.class, accessKey,
                enterprise.symbol(), period, startDate, stopDate).getBody();
        if (historicalResult == null) {
            throw new NullPointerException("Body is missing!");
        }

        var historicalQuotes = new ArrayList<HistoricalQuote>();
        for (var quotes : historicalResult.data()) {
            historicalQuotes.add(new HistoricalQuote(quotes.localDate(), quotes.close()));
        }

        historicalQuotes.sort(Comparator.comparing(HistoricalQuote::date).reversed());
        return historicalQuotes;
    }

}
