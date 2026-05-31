package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;

import java.math.BigDecimal;
import java.util.List;

public interface CashbackRepository {
    void save(CashbackRecord record);

    List<CashbackRecord> findByCustomerId(String customerId);

    BigDecimal totalForProductCategory(String productCategory);

    long countForProductCategory(String productCategory);
}
