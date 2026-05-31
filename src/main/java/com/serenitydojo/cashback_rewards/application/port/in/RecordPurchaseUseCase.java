package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

public interface RecordPurchaseUseCase {
    void record(String customerId, String merchantName, String mcc, BigDecimal amount, Instant purchasedAt);
}
