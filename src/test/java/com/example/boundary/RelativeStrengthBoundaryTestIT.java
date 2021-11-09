package com.example.boundary;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.boundary.api.RelativeStrengthResult;

@SpringBootTest
class RelativeStrengthBoundaryTestIT {

    @Autowired
    private RelativeStrengthBoundary boundary;

    @Test
    void shouldReturnRelativeStrength() {
        RelativeStrengthResult result = boundary.relativeStrength("AAPL");

        assertThat(result).isNotNull();
        assertThat(result.enterprise().symbol()).isEqualTo("AAPL");
    }
}
