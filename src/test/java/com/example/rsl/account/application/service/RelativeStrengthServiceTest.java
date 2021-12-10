package com.example.rsl.account.application.service;

import static com.example.rsl.account.adapter.in.web.Exchange.HDAX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.rsl.account.adapter.in.web.RelativeStrengthResult;
import com.example.rsl.account.adapter.in.web.RelativeStrengthResultWrapper;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthCommand;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthsCommand;
import com.example.rsl.account.application.port.out.EnterpriseLoader;
import com.example.rsl.account.application.port.out.HistoricalQuotesFetcher;
import com.example.rsl.account.application.port.out.SymbolLookUp;
import com.example.rsl.account.domain.Enterprise;
import com.example.rsl.account.domain.HistoricalQuote;

@ExtendWith(MockitoExtension.class)
class RelativeStrengthServiceTest {

    @Mock
    private SymbolLookUp symbolLookUp;

    @Mock
    private EnterpriseLoader enterpriseLoader;

    @Mock
    private HistoricalQuotesFetcher historicalQuotesFetcher;

    @InjectMocks
    private RelativeStrengthService relativeStrengthService;

    @Test
    void shouldCalculateRelativeStrength() {
        final LocalDate date = LocalDate.of(2021, Month.NOVEMBER, 30);
        final int period = 135;
        final Enterprise enterprise = new Enterprise("AaBbCc", "ABC");
        when(symbolLookUp.lookUp("ABC")).thenReturn(enterprise);
        when(historicalQuotesFetcher.fetch(enterprise, eq(LocalDate.now()), period)).thenReturn(generateHistoricalQuotes(date, period));

        CalculateRelativeStrengthCommand command = new CalculateRelativeStrengthCommand("ABC", period);
        RelativeStrengthResult relativeStrengthResult = relativeStrengthService.calculateRelativeStrength(command);

        assertThat(relativeStrengthResult.enterprise()).isEqualTo(enterprise);
        assertThat(relativeStrengthResult.relativeStrength().date()).isEqualTo(date);
        assertThat(relativeStrengthResult.relativeStrength().rsl()).isEqualTo(1.0);
    }

    private List<HistoricalQuote> generateHistoricalQuotes(LocalDate date, int period) {
     return generateHistoricalQuotes(date, period, 0);
    }

    private List<HistoricalQuote> generateHistoricalQuotes(LocalDate date, int period, int factor) {
        List<HistoricalQuote> historicalQuotes = new ArrayList<>();
        for (int i = 0; i < period; ++i) {
            historicalQuotes.add(new HistoricalQuote(date.minusDays(i), 1000.00 - (i * factor)));
        }

        return historicalQuotes;
    }

    @Test
    void shouldCalculateRelativeStrengths() {
        final LocalDate date = LocalDate.of(2021, Month.NOVEMBER, 30);
        final int period = 135;
        final Enterprise beiersdorf = new Enterprise("BEIERSDORF AG O.N.", "BEI.XETRA");
        final Enterprise brenntag = new Enterprise("BRENNTAG SE NA O.N.", "BNR.XETRA");
        when(enterpriseLoader.loadExchange(HDAX)).thenReturn(List.of(beiersdorf, brenntag));
        when(historicalQuotesFetcher.fetchAsync(beiersdorf, eq(LocalDate.now()), period)).thenReturn(generateCompletableFutureHistoricalQuotes(date, period));
        when(historicalQuotesFetcher.fetchAsync(brenntag, eq(LocalDate.now()),period)).thenReturn(generateCompletableFutureHistoricalQuotes(date, period, 3));

        CalculateRelativeStrengthsCommand command = new CalculateRelativeStrengthsCommand(HDAX, period);
        RelativeStrengthResultWrapper wrapper = relativeStrengthService.calculateRelativeStrengths(command);

        assertThat(wrapper.relativeStrengthResults().size()).isEqualTo(3);

        assertThat(wrapper.relativeStrengthResults().get(0).enterprise()).isEqualTo(brenntag);
        assertThat(wrapper.relativeStrengthResults().get(0).relativeStrength().date()).isEqualTo(date);
        assertThat(wrapper.relativeStrengthResults().get(0).relativeStrength().rsl()).isEqualTo(1.2515, withPrecision(0.0001));

        assertThat(wrapper.relativeStrengthResults().get(1).enterprise()).isEqualTo(new Enterprise(HDAX.name(), HDAX.name()));
        assertThat(wrapper.relativeStrengthResults().get(1).relativeStrength().date()).isEqualTo(date);
        assertThat(wrapper.relativeStrengthResults().get(1).relativeStrength().rsl()).isEqualTo(1.1257, withPrecision(0.0001));

        assertThat(wrapper.relativeStrengthResults().get(2).enterprise()).isEqualTo(beiersdorf);
        assertThat(wrapper.relativeStrengthResults().get(2).relativeStrength().date()).isEqualTo(date);
        assertThat(wrapper.relativeStrengthResults().get(2).relativeStrength().rsl()).isEqualTo(1.0);
    }

    private CompletableFuture<List<HistoricalQuote>> generateCompletableFutureHistoricalQuotes(LocalDate date, int period) {
        return CompletableFuture.completedFuture(generateHistoricalQuotes(date, period));
    }

    private CompletableFuture<List<HistoricalQuote>> generateCompletableFutureHistoricalQuotes(LocalDate date, int period, int factor) {
        return CompletableFuture.completedFuture(generateHistoricalQuotes(date, period, factor));
    }

}
