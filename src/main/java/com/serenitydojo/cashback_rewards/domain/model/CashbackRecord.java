package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;

public record CashbackRecord(String customerId, String merchantName, String productCategory, BigDecimal cashbackAmount) {
}
