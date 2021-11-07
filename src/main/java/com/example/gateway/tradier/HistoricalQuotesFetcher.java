package com.example.gateway.tradier;

import com.example.gateway.tradier.dto.Historical;
import com.example.model.Enterprise;
import com.example.model.HistoricalQuote;
import com.example.model.Interval;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricalQuotesFetcher {

    public List<HistoricalQuote> fetch(Enterprise enterprise, Interval interval, int period)  {

        LocalDate stopDate = LocalDate.now();
        int weeks = period / 5;
        int minusWeeks = weeks * 7 + 2; // TODO: if Sunday if Saturday
        LocalDate startDate = stopDate.minusWeeks(minusWeeks);

        final HttpUriRequest request = RequestBuilder
                .get("https://sandbox.tradier.com/v1/markets/history")
                .addHeader("Authorization", "Bearer TODO")
                .addHeader("Accept", "application/json")
                .addParameter("symbol", enterprise.getSymbol())
                .addParameter("interval", map(interval))
                .addParameter("start", startDate.toString())
                .addParameter("end", stopDate.toString())
                .build();

        try {
            final HttpResponse response = HttpClientBuilder.create().build().execute(request);
            final String jsonString = EntityUtils.toString(response.getEntity());
            Historical historical = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(jsonString, Historical.class);
            return historical.getHistory().getDay().stream()
                    .map(quotes -> new HistoricalQuote(quotes.getDate(), quotes.getClose()))
                    .sorted(Comparator.comparing(HistoricalQuote::getDate).reversed())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private String map(Interval interval) {
        return switch (interval) {
            case WEEKLY -> "weekly";
            case DAILY -> "daily";
        };
    }
}
