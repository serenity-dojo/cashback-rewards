package com.serenitydojo.cashback_rewards.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CashbackCalculator")
class CashbackCalculatorTest {

    @Test
    @DisplayName("multiplies the purchase amount by the merchant's cashback rate")
    void multipliesPurchaseAmountByRate() {
        BigDecimal cashback = CashbackCalculator.calculate(
                new BigDecimal("80.00"),
                new BigDecimal("0.03"));

        assertThat(cashback).isEqualByComparingTo("2.40");
    }

    @Test
    @DisplayName("rounds a half-cent tie to the nearest even cent (banker's rounding)")
    void roundsHalfCentTieToEvenCent() {
        // 33.50 * 0.05 = 1.6750 — exactly halfway between 1.67 and 1.68;
        // half-even rounds to the even cent 1.68 (DOWN would truncate to 1.67).
        BigDecimal cashback = CashbackCalculator.calculate(
                new BigDecimal("33.50"),
                new BigDecimal("0.05"));

        assertThat(cashback).isEqualByComparingTo("1.68");
    }
}
