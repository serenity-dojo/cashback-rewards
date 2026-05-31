package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;

public record ProductCategory(String mcc, String name, BigDecimal cashbackRate) {

    private static final String UNMAPPED_CATEGORY_NAME = "Other";

    public static ProductCategory unmapped(String mcc, BigDecimal defaultRate) {
        return new ProductCategory(mcc, UNMAPPED_CATEGORY_NAME, defaultRate);
    }
}