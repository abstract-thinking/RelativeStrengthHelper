package com.example.gateway.marketstack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.gateway.marketstack.dto.HistoricalResult;
import com.example.gateway.marketstack.dto.Quotes;
import com.example.model.Enterprise;
import com.example.model.HistoricalQuote;

@Service
public class HistoricalQuotesFetcher {

    private static final String URL = "http://api.marketstack.com/v1/eod?access_key="
            + "{access_key}&symbols={symbol}&limit={limit}&date_from={start_date}&date_to={stop_date}";

    @Value("${access.key}")
    private String accessKey;

    public List<HistoricalQuote> fetch(Enterprise enterprise, int period) {
        LocalDate stopDate = LocalDate.now();
        LocalDate startDate = stopDate.minusYears(1);

        HistoricalResult historicalResult = new RestTemplate().getForEntity(URL, HistoricalResult.class, accessKey,
                enterprise.getSymbol(), period, startDate, stopDate).getBody();
        if (historicalResult == null) {
            throw new NullPointerException("Body is missing!");
        }

        List<HistoricalQuote> historicalQuotes = new ArrayList<>();
        for (Quotes quotes : historicalResult.data()) {
            historicalQuotes.add(new HistoricalQuote(quotes.localDate(), quotes.close()));
        }

        historicalQuotes.sort(Comparator.comparing(HistoricalQuote::date).reversed());
        return historicalQuotes;
    }

}
