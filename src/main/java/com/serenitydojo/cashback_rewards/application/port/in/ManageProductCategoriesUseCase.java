package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;

import java.math.BigDecimal;

public interface ManageProductCategoriesUseCase {
    void register(ProductCategory category);

    void configureDefaultRate(BigDecimal rate);
}