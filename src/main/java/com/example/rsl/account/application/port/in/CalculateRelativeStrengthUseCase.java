package com.example.rsl.account.application.port.in;

import com.example.rsl.account.adapter.in.web.RelativeStrengthResult;
import com.example.rsl.account.adapter.in.web.RelativeStrengthResultWrapper;

public interface CalculateRelativeStrengthUseCase {

    RelativeStrengthResult calculateRelativeStrength(CalculateRelativeStrengthCommand calculateRelativeStrengthCommand);

    RelativeStrengthResultWrapper calculateRelativeStrengths(CalculateRelativeStrengthsCommand calculateRelativeStrengthsCommand);
}
