package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CashbackRecord(String customerId, String merchantName, String productCategory,
                             BigDecimal cashbackAmount, Instant postedAt) {
}
