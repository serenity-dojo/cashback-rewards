package com.serenitydojo.cashback_rewards.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CashbackCalculator {

    private CashbackCalculator() {
    }

    public static BigDecimal calculate(BigDecimal purchaseAmount, BigDecimal cashbackRate) {
        return purchaseAmount.multiply(cashbackRate)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
