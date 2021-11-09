package com.example.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.boundary.api.Exchange;
import com.example.model.Enterprise;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.SneakyThrows;

@Service
public class EnterpriseFetcher {

    private static final String URL = "http://api.marketstack.com/v1/tickers/{symbol}?access_key={accessKey}";

    @Value("${access.key}")
    private String accessKey;

    public Enterprise lookUp(String symbol) {
        record Description(String symbol, String name) {}

        var description = new RestTemplate().getForEntity(URL, Description.class, symbol, accessKey).getBody();
        if (description == null) {
            return new Enterprise("", symbol);
        }

        return new Enterprise(description.name(), description.symbol());
    }

    public List<Enterprise> fetch(Exchange exchange) {
        return switch (exchange) {
            case HDAX -> readHDAX();
            case NASDAQ, SP500 -> Collections.emptyList();
        };
    }

    @SneakyThrows
    private List<Enterprise> readHDAX() {
        var inputStream = EnterpriseFetcher.class.getClassLoader().getResourceAsStream("hdax_symbols.csv");
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found");
        }

        var enterprises = new ArrayList<Enterprise>();
        var reader = new CSVReader(new InputStreamReader(inputStream));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            enterprises.add(new Enterprise(nextLine[1], nextLine[0]));
        }

        return enterprises;
    }
}
