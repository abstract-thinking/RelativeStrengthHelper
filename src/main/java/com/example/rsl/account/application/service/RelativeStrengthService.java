package com.example.rsl.account.application.service;

import static com.example.rsl.account.application.service.RelativeStrengthCalculator.calculate;
import static java.util.Comparator.reverseOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.example.rsl.account.adapter.in.web.RelativeStrengthResult;
import com.example.rsl.account.adapter.in.web.RelativeStrengthResultWrapper;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthCommand;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthUseCase;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthsCommand;
import com.example.rsl.account.application.port.out.EnterpriseLoader;
import com.example.rsl.account.application.port.out.HistoricalQuotesFetcher;
import com.example.rsl.account.application.port.out.SymbolLookUp;
import com.example.rsl.account.domain.Enterprise;
import com.example.rsl.account.domain.RelativeStrength;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
@Service
public class RelativeStrengthService implements CalculateRelativeStrengthUseCase {

    private SymbolLookUp symbolLookUp;

    private EnterpriseLoader enterpriseLoader;

    private HistoricalQuotesFetcher historicalQuotesFetcher;


    @SneakyThrows
    @Override
    public RelativeStrengthResult calculateRelativeStrength(CalculateRelativeStrengthCommand calculateRelativeStrengthCommand) {
        final var enterprise = symbolLookUp.lookUp(calculateRelativeStrengthCommand.symbol());
        final var endDate = LocalDate.now();
        final var historicalQuotes = historicalQuotesFetcher.fetch(enterprise, endDate, calculateRelativeStrengthCommand.period());

        return new RelativeStrengthResult(enterprise, calculate(historicalQuotes, calculateRelativeStrengthCommand.period(), enterprise));
    }

    @SneakyThrows
    @Override
    public RelativeStrengthResultWrapper calculateRelativeStrengths(CalculateRelativeStrengthsCommand calculateRelativeStrengthsCommand) {
        final var enterprises = enterpriseLoader.loadExchange(calculateRelativeStrengthsCommand.exchange());

        LocalDate endDate = LocalDate.now();
        final var futures = new HashMap<Enterprise, CompletableFuture<RelativeStrength>>();
        for (var enterprise : enterprises) {
            final var historicalQuotes = historicalQuotesFetcher
                    .fetchAsync(enterprise, endDate, calculateRelativeStrengthsCommand.period())
                    .thenApply(historicalQuote -> calculate(historicalQuote, calculateRelativeStrengthsCommand.period(), enterprise));
            futures.put(enterprise, historicalQuotes);
        }

        final var result = new ArrayList<RelativeStrengthResult>();
        for (Map.Entry<Enterprise, CompletableFuture<RelativeStrength>> future : futures.entrySet()) {
            result.add(new RelativeStrengthResult(future.getKey(), future.getValue().get()));
        }

        result.add(calculateIndexRsl(calculateRelativeStrengthsCommand.exchange().name(), result));
        result.sort(Comparator.comparing(
                relativeStrengthResult -> relativeStrengthResult.relativeStrength().rsl(), reverseOrder()));

        // TODO: Better way from backend?
        LocalDate dataEndDate = result.get(0).relativeStrength().date();

        return new RelativeStrengthResultWrapper(
                calculateRelativeStrengthsCommand.exchange().name(), dataEndDate, result);
    }

    private RelativeStrengthResult calculateIndexRsl(String exchangeName, List<RelativeStrengthResult> result) {
        double exchangeRsl = result.stream().mapToDouble(rsl -> rsl.relativeStrength().rsl()).sum() / result.size();

        return new RelativeStrengthResult(new Enterprise(exchangeName, exchangeName),
                new RelativeStrength(result.get(0).relativeStrength().date(), exchangeRsl));
    }
}
