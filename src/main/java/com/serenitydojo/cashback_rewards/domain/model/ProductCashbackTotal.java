package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The total cashback paid for a single product (category), aggregated across
 * all customers, together with the number of cashback payments counted.
 *
 * <p>The total is always expressed in whole cents (scale 2), consistent with how
 * individual cashback amounts are recorded.
 */
public record ProductCashbackTotal(String product, BigDecimal totalCashback, long recordCount) {

    public ProductCashbackTotal {
        totalCashback = totalCashback.setScale(2, RoundingMode.DOWN);
    }
}
