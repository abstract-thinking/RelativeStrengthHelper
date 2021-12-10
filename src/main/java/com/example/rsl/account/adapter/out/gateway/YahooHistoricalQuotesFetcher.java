package com.example.rsl.account.adapter.out.gateway;

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.rsl.account.application.port.out.HistoricalQuotesFetcher;
import com.example.rsl.account.domain.Enterprise;
import com.example.rsl.account.domain.HistoricalQuote;
import com.opencsv.CSVReader;

import lombok.SneakyThrows;

@Primary
@Service
public class YahooHistoricalQuotesFetcher implements HistoricalQuotesFetcher {

    private static final String URL = "https://query1.finance.yahoo.com/v7/finance/download/{symbol}?period1={startDate"
            + "}&period2={endDate}&interval=1d&events=history";

    @SneakyThrows
    @Override
    public List<HistoricalQuote> fetch(Enterprise enterprise, LocalDate endDate, int period) {
        long endDateInEpochSecond = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        int minusDays = ((period / 5) * 7) + 2;
        long startDateInEpochSecond = endDate.minusDays(minusDays).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        Map<String, String> params = new HashMap<>();
        params.put("symbol", enterprise.symbol().concat(".DE"));
        params.put("endDate", String.valueOf(endDateInEpochSecond));
        params.put("startDate", String.valueOf(startDateInEpochSecond));

        File file = new RestTemplate().execute(URL, HttpMethod.GET, null, clientHttpResponse -> {
            File ret = File.createTempFile("download", "tmp");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        }, params);

        if (file == null) {
            throw new NullPointerException("Missing file");
        }

        List<HistoricalQuote> historicalQuotes = new ArrayList<>();
        try (var reader = new CSVReader(new FileReader(file))) {
            reader.skip(1);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                historicalQuotes.add(new HistoricalQuote(LocalDate.parse(nextLine[0]), Double.valueOf(nextLine[4])));
            }
        }

        historicalQuotes.sort(Comparator.comparing(HistoricalQuote::date).reversed());

        return historicalQuotes;
    }

    @Override
    public CompletableFuture<List<HistoricalQuote>> fetchAsync(Enterprise enterprise, LocalDate endDate, int period) {
        return completedFuture(fetch(enterprise, endDate, period));
    }
}
