package com.example.gateway;

import com.example.model.Enterprise;
import com.example.model.Exchange;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnterpriseFetcher {

    public List<Enterprise> fetch(Exchange exchange) {
        return switch (exchange) {
            case HDAX -> readHDAX();
            case NASDAQ -> Collections.emptyList();
            case SP500 -> Collections.emptyList();
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
