package com.example.gateway;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.model.Enterprise;
import com.example.boundary.api.Exchange;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EnterpriseFetcher {

    @Value("${access.key}")
    private String accessKey;

    public Enterprise lookUp(String symbol) {
        final HttpUriRequest request = RequestBuilder
                .get("http://api.marketstack.com/v1/tickers/" + symbol + "?access_key=" + accessKey)
                .build();

        try {
            final HttpResponse response = HttpClientBuilder.create().build().execute(request);
            final String jsonString = EntityUtils.toString(response.getEntity());

            record Description(String symbol, String name) {}
            Description description = new ObjectMapper()
                    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(jsonString, Description.class);

            return new Enterprise(description.name(), description.symbol());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Enterprise("", symbol);
    }

    public List<Enterprise> fetch(Exchange exchange) {
        return switch (exchange) {
            case HDAX -> readHDAX();
            case NASDAQ, SP500 -> Collections.emptyList();
        };
    }

    private List<Enterprise> readHDAX() {
        InputStream inputStream = EnterpriseFetcher.class.getResourceAsStream("hdax_symbols.csv");
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found");
        }

        List<Enterprise> enterprises = new ArrayList<>();
        try (HSSFWorkbook workbook = new HSSFWorkbook(inputStream)) {
            HSSFSheet sheet = workbook.getSheetAt(1);
            for (Row row : sheet) {
                if (row.getCell(1).getStringCellValue().equals("HDAX PERFORMANCE-INDEX")) {
                    enterprises.add(
                            new Enterprise(row.getCell(4).getStringCellValue(), row.getCell(5).getStringCellValue())
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return enterprises;
    }
}
