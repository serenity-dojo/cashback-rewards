package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;

public record MinimumPurchaseThreshold(BigDecimal amount) {

    public static final MinimumPurchaseThreshold DEFAULT =
            new MinimumPurchaseThreshold(new BigDecimal("1.00"));

    public boolean isMetBy(BigDecimal purchaseAmount) {
        return purchaseAmount.compareTo(amount) >= 0;
    }
}
