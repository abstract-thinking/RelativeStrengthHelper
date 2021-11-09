package com.example.gateway.marketstack;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.gateway.marketstack.dto.HistoricalResult;
import com.example.gateway.marketstack.dto.Quotes;
import com.example.model.Enterprise;
import com.example.model.HistoricalQuote;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class HistoricalQuotesFetcher {

    @Value("${access.key}")
    private String accessKey;

    public List<HistoricalQuote> fetch(Enterprise enterprise, int period)  {

        LocalDate stopDate = LocalDate.now();
        int weeks = period / 5;
        int minusWeeks = weeks * 7 + 2; // TODO: if Sunday if Saturday
        LocalDate startDate = stopDate.minusWeeks(minusWeeks);

        final HttpUriRequest request = RequestBuilder
                .get("http://api.marketstack.com/v1/eod" +
                        "?access_key=" + accessKey +
                        "&symbols=" + enterprise.getSymbol() +
                        "&limit=" + period +
                        "&date_from=" + startDate +
                        "&date_to=" + stopDate)
                .build();

        try {
            final HttpResponse response = HttpClientBuilder.create().build().execute(request);
            final String jsonString = EntityUtils.toString(response.getEntity());
            HistoricalResult historical = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(jsonString, HistoricalResult.class);
            List<HistoricalQuote> historicalQuotes = new ArrayList<>();
            for (Quotes quotes : historical.data()) {
                historicalQuotes.add(new HistoricalQuote(quotes.localDate(), quotes.close()));
            }

            historicalQuotes.sort(Comparator.comparing(HistoricalQuote::date).reversed());
            return historicalQuotes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

}
