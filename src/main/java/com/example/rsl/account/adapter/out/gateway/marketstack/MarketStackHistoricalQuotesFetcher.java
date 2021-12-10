package com.example.rsl.account.adapter.out.gateway.marketstack;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.rsl.account.application.port.out.HistoricalQuotesFetcher;
import com.example.rsl.account.domain.Enterprise;
import com.example.rsl.account.domain.HistoricalQuote;

@Service
class MarketStackHistoricalQuotesFetcher implements HistoricalQuotesFetcher {

    private static final String URL = "http://api.marketstack.com/v1/eod?access_key="
            + "{access_key}&symbols={symbol}&limit={limit}&date_from={start_date}&date_to={stop_date}";

    @Value("${access.key}")
    private String accessKey;

    private final Map<Enterprise, List<HistoricalQuote>> historicalQuotesToRefresh = new HashMap<>();

    private Optional<List<HistoricalQuote>> getCachedHistoricalQuotes(final Enterprise enterprise, final LocalDate date) {
        var cachedHistoricalQuotes = historicalQuotesToRefresh.get(enterprise);
        if (cachedHistoricalQuotes == null || cachedHistoricalQuotes.isEmpty()) {
            return Optional.empty();
        }

        if (cachedHistoricalQuotes.get(0).date().equals(date)) {
            return Optional.of(cachedHistoricalQuotes);
        }

        return Optional.empty();
    }

    public List<HistoricalQuote> fetch(final Enterprise enterprise, LocalDate stopDate, final int period) {
        var startDate = stopDate.minusYears(1);
        final Optional<List<HistoricalQuote>> cachedHistoricalQuotes = getCachedHistoricalQuotes(enterprise, startDate);
        if (cachedHistoricalQuotes.isPresent()) {
            return cachedHistoricalQuotes.get();
        }

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

        record Pagination(int limit, int offset, int count, int total) {
        }

        record HistoricalResult(Pagination pagination, List<Quotes> data) {
        }

        // Region issue :-(
        final var historicalResult = new RestTemplate()
                .getForEntity(URL, HistoricalResult.class, accessKey, enterprise.symbol().replace(".DE", ".XETRA"), period, startDate, stopDate)
                .getBody();
        if (historicalResult == null) {
            throw new NullPointerException("Body is missing!");
        }

        final var historicalQuotes = new ArrayList<HistoricalQuote>();
        for (var quotes : historicalResult.data()) {
            historicalQuotes.add(new HistoricalQuote(quotes.localDate(), quotes.close()));
        }

        historicalQuotes.sort(Comparator.comparing(HistoricalQuote::date).reversed());
        historicalQuotesToRefresh.put(enterprise, historicalQuotes);

        return historicalQuotes.subList(0, period);
    }

    @Async
    public CompletableFuture<List<HistoricalQuote>> fetchAsync(Enterprise enterprise, LocalDate stopDate, int period) {
        return CompletableFuture.completedFuture(fetch(enterprise, stopDate, period));
    }
}
