package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaCashbackRepository.class)
@DisplayName("JpaCashbackRepository")
class JpaCashbackRepositoryTest {

    private static final Instant POSTED_AT = Instant.parse("2026-05-01T10:00:00Z");

    @Autowired
    JpaCashbackRepository repository;

    @Test
    @DisplayName("returns the cashback record previously saved for a given customer")
    void findsSavedCashbackRecordByCustomerId() {
        repository.save(new CashbackRecord("cust-001", "GreenGrocer", "Groceries", new BigDecimal("2.40"), POSTED_AT));

        assertThat(repository.findByCustomerId("cust-001"))
                .singleElement()
                .satisfies(record -> {
                    assertThat(record.customerId()).isEqualTo("cust-001");
                    assertThat(record.merchantName()).isEqualTo("GreenGrocer");
                    assertThat(record.productCategory()).isEqualTo("Groceries");
                    assertThat(record.cashbackAmount()).isEqualByComparingTo("2.40");
                });
    }

    @Test
    @DisplayName("returns every record earned by a customer and excludes other customers' records")
    void returnsAllRecordsForACustomerAndExcludesOthers() {
        repository.save(new CashbackRecord("cust-001", "GreenGrocer", "Groceries", new BigDecimal("2.40"), POSTED_AT));
        repository.save(new CashbackRecord("cust-001", "FuelCo", "Fuel", new BigDecimal("1.00"), POSTED_AT));
        repository.save(new CashbackRecord("cust-999", "OtherShop", "Other", new BigDecimal("0.50"), POSTED_AT));

        assertThat(repository.findByCustomerId("cust-001"))
                .extracting(CashbackRecord::merchantName, CashbackRecord::productCategory)
                .containsExactlyInAnyOrder(
                        tuple("GreenGrocer", "Groceries"),
                        tuple("FuelCo", "Fuel"));
    }

    @Test
    @DisplayName("totals the cashback paid for a product category across all customers")
    void totalsCashbackForAProductCategoryAcrossCustomers() {
        repository.save(new CashbackRecord("cust-001", "Market-A", "Groceries", new BigDecimal("2.40"), POSTED_AT));
        repository.save(new CashbackRecord("cust-002", "Market-B", "Groceries", new BigDecimal("1.60"), POSTED_AT));
        repository.save(new CashbackRecord("cust-003", "FuelCo", "Fuel", new BigDecimal("5.00"), POSTED_AT));

        assertThat(repository.totalForProductCategory("Groceries")).isEqualByComparingTo("4.00");
    }

    @Test
    @DisplayName("totals zero for a product category that has no cashback records")
    void totalsZeroForAProductCategoryWithNoRecords() {
        assertThat(repository.totalForProductCategory("Travel")).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("counts the cashback records for a product category across all customers")
    void countsCashbackRecordsForAProductCategoryAcrossCustomers() {
        repository.save(new CashbackRecord("cust-001", "Market-A", "Groceries", new BigDecimal("2.40"), POSTED_AT));
        repository.save(new CashbackRecord("cust-002", "Market-B", "Groceries", new BigDecimal("1.60"), POSTED_AT));
        repository.save(new CashbackRecord("cust-003", "FuelCo", "Fuel", new BigDecimal("5.00"), POSTED_AT));

        assertThat(repository.countForProductCategory("Groceries")).isEqualTo(2L);
    }
}
