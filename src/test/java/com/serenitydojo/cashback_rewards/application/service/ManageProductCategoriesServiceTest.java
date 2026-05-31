package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.adapter.out.persistence.InMemoryCategoryRepository;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ManageProductCategoriesService")
class ManageProductCategoriesServiceTest {

    @Test
    @DisplayName("makes a registered category retrievable from the repository by MCC")
    void registeredCategoryBecomesRetrievable() {
        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        ManageProductCategoriesService service = new ManageProductCategoriesService(categories);
        ProductCategory groceries = new ProductCategory("5411", "Groceries", new BigDecimal("0.02"));

        service.register(groceries);

        assertThat(categories.findByMcc("5411")).contains(groceries);
    }

    @Test
    @DisplayName("configures the default cashback rate used for unmapped MCCs")
    void configuresTheDefaultRate() {
        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        ManageProductCategoriesService service = new ManageProductCategoriesService(categories);

        service.configureDefaultRate(new BigDecimal("0.005"));

        assertThat(categories.defaultRate()).isEqualByComparingTo("0.005");
    }
}
