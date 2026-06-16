package com.serenitydojo.cashback_rewards.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MinimumPurchaseThreshold")
class MinimumPurchaseThresholdTest {

    @ParameterizedTest(name = "a ${0} purchase {1} the $1.00 minimum")
    @CsvSource(textBlock = """
             0.50,  is below,         false
             0.99,  is below,         false
             0.999, is just below,    false
             1.00,  is exactly at,    true
            25.00,  is above,         true
            """)
    @DisplayName("is met only by purchases at or above the minimum amount")
    void isMetByPurchasesAtOrAboveTheMinimum(String purchaseAmount, String description, boolean expected) {
        boolean met = MinimumPurchaseThreshold.DEFAULT.isMetBy(new BigDecimal(purchaseAmount));

        assertThat(met).isEqualTo(expected);
    }
}
