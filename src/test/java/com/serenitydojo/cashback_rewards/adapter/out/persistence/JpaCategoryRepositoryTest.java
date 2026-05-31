package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaCategoryRepository.class)
@DisplayName("JpaCategoryRepository")
class JpaCategoryRepositoryTest {

    @Autowired
    JpaCategoryRepository repository;

    @Test
    @DisplayName("returns an empty result for an MCC that was never saved")
    void returnsEmptyForUnknownMcc() {
        assertThat(repository.findByMcc("9999")).isEmpty();
    }

    @Test
    @DisplayName("returns a previously saved category when looked up by MCC")
    void findsSavedCategoryByMcc() {
        repository.save(new ProductCategory("5411", "Groceries", new BigDecimal("0.02")));

        assertThat(repository.findByMcc("5411")).hasValueSatisfying(category -> {
            assertThat(category.mcc()).isEqualTo("5411");
            assertThat(category.name()).isEqualTo("Groceries");
            assertThat(category.cashbackRate()).isEqualByComparingTo("0.02");
        });
    }

    @Test
    @DisplayName("stores and returns the configured default cashback rate")
    void storesConfiguredDefaultRate() {
        repository.saveDefaultRate(new BigDecimal("0.005"));

        assertThat(repository.defaultRate()).isEqualByComparingTo("0.005");
    }

    @Test
    @DisplayName("reconfiguring the default rate overwrites the previously stored value")
    void reconfiguringDefaultRateOverwritesPreviousValue() {
        repository.saveDefaultRate(new BigDecimal("0.005"));
        repository.saveDefaultRate(new BigDecimal("0.01"));

        assertThat(repository.defaultRate()).isEqualByComparingTo("0.01");
    }
}
