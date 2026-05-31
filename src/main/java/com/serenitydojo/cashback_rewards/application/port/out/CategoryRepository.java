package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;

import java.math.BigDecimal;
import java.util.Optional;

public interface CategoryRepository {
    void save(ProductCategory category);

    void saveDefaultRate(BigDecimal rate);

    BigDecimal defaultRate();

    Optional<ProductCategory> findByMcc(String mcc);
}