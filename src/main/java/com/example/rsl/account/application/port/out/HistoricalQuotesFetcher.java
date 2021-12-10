package com.example.rsl.account.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.rsl.account.domain.Enterprise;
import com.example.rsl.account.domain.HistoricalQuote;

public interface HistoricalQuotesFetcher {

    List<HistoricalQuote> fetch(Enterprise enterprise, LocalDate endDate,  int period);

    CompletableFuture<List<HistoricalQuote>> fetchAsync(Enterprise enterprise, LocalDate endDate, int period);
}
