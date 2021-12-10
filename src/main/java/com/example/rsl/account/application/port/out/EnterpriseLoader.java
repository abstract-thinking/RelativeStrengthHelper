package com.example.rsl.account.application.port.out;

import java.util.List;

import com.example.rsl.account.adapter.in.web.Exchange;
import com.example.rsl.account.domain.Enterprise;

public interface EnterpriseLoader {

    List<Enterprise> loadExchange(Exchange exchange);
}
