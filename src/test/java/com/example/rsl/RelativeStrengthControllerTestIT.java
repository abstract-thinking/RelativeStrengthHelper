package com.example.rsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.rsl.account.adapter.in.web.RelativeStrengthController;
import com.example.rsl.account.adapter.in.web.RelativeStrengthResult;

@SpringBootTest
class RelativeStrengthControllerTestIT {

    @Autowired
    private RelativeStrengthController boundary;

    @Test
    void shouldReturnRelativeStrength() {
        RelativeStrengthResult result = boundary.relativeStrength("AAPL");

        assertThat(result).isNotNull();
        assertThat(result.enterprise().symbol()).isEqualTo("AAPL");
    }
}
