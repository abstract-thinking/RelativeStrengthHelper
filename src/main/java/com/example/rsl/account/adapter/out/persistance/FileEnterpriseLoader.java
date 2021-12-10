package com.example.rsl.account.adapter.out.persistance;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rsl.account.adapter.in.web.Exchange;
import com.example.rsl.account.application.port.out.EnterpriseLoader;
import com.example.rsl.account.domain.Enterprise;
import com.opencsv.CSVReader;

import lombok.SneakyThrows;

@Service
public class FileEnterpriseLoader implements EnterpriseLoader {

    public List<Enterprise> loadExchange(Exchange exchange) {
        return switch (exchange) {
            case HDAX -> readHDAX();
            case NASDAQ, SP500 -> Collections.emptyList();
        };
    }

    @SneakyThrows
    private List<Enterprise> readHDAX() {
        var inputStream = FileEnterpriseLoader.class.getClassLoader().getResourceAsStream("hdax_symbols.csv");
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
