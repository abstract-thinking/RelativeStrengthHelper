package com.example.rsl.account.adapter.out.gateway.yahoo.api;

import lombok.Data;
import lombok.NoArgsConstructor;

public class Document {
    public String industryName;

    public String industryLink;

    public String symbol;

    public RegularMarketChange regularMarketChange;

    public RegularMarketPercentChange regularMarketPercentChange;

    public int rank;

    public String exchange;

    public String shortName;

    public String quoteType;

    public RegularMarketPrice regularMarketPrice;
}
