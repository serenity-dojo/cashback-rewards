package com.serenitydojo.cashback_rewards.domain.service;

import java.math.BigDecimal;

public final class CashbackCalculator {

    private CashbackCalculator() {
    }

    public static BigDecimal calculate(BigDecimal purchaseAmount, BigDecimal cashbackRate) {
        return purchaseAmount.multiply(cashbackRate);
    }
}
