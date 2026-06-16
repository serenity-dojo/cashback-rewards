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
    void roundsHalfCentTieToNearestEvenCent() {
        BigDecimal cashback = CashbackCalculator.calculate(
                new BigDecimal("5.00"),
                new BigDecimal("0.005"));

        assertThat(cashback).isEqualByComparingTo("0.02");
    }

    @Test
    @DisplayName("rounds an above-half cashback up using banker's rounding (half-even)")
    void roundsAboveHalfCashbackUp() {
        BigDecimal cashback = CashbackCalculator.calculate(
                new BigDecimal("166.51"),
                new BigDecimal("0.01"));

        assertThat(cashback).isEqualByComparingTo("1.67");
    }
}
