package com.serenitydojo.cashback_rewards.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductCategory")
class ProductCategoryTest {

    @Test
    @DisplayName("unmapped builds an \"Other\" category carrying the supplied MCC and the default rate")
    void unmappedUsesDefaultRateUnderTheOtherCategory() {
        ProductCategory category = ProductCategory.unmapped("5912", new BigDecimal("0.005"));

        assertThat(category.mcc()).isEqualTo("5912");
        assertThat(category.name()).isEqualTo("Other");
        assertThat(category.cashbackRate()).isEqualByComparingTo("0.005");
    }
}