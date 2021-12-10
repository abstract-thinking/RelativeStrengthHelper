package com.example.rsl.account.adapter.in.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.rsl.account.application.port.in.CalculateRelativeStrengthCommand;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthUseCase;
import com.example.rsl.account.application.port.in.CalculateRelativeStrengthsCommand;

@WebMvcTest(controllers = RelativeStrengthController.class)
class RelativeStrengthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculateRelativeStrengthUseCase calculateRelativeStrengthUseCase;

    @Test
    void testCalculateRelativeStrength() throws Exception {
        mockMvc.perform(get("/rs/symbols/{symbol}", "ABC"))
                .andExpect(status().isOk());

        then(calculateRelativeStrengthUseCase)
                .should()
                .calculateRelativeStrength(eq(new CalculateRelativeStrengthCommand("ABC", 135)));
    }

    @Test
    void testCalculateRelativeStrengths() throws Exception {
        mockMvc.perform(get("/rs/exchanges/{exchange}", "NASDAQ"))
                .andExpect(status().isOk());

        then(calculateRelativeStrengthUseCase)
                .should()
                .calculateRelativeStrengths(eq(new CalculateRelativeStrengthsCommand(Exchange.NASDAQ, 135)));
    }

}
