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
}
