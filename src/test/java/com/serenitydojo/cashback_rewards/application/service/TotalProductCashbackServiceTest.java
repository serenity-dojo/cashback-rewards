package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.adapter.out.persistence.InMemoryCashbackRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import com.serenitydojo.cashback_rewards.domain.model.ProductCashbackTotal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TotalProductCashbackService")
class TotalProductCashbackServiceTest {

    private static final Instant POSTED_AT = Instant.parse("2026-05-01T10:00:00Z");

    @Test
    @DisplayName("totals the cashback paid for a product across all customers, excluding other products")
    void totalsCashbackAcrossCustomersForAProduct() {
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        cashbacks.save(new CashbackRecord("cust-001", "Market-A", "Groceries", new BigDecimal("2.40"), POSTED_AT));
        cashbacks.save(new CashbackRecord("cust-002", "Market-B", "Groceries", new BigDecimal("1.60"), POSTED_AT));
        cashbacks.save(new CashbackRecord("cust-003", "FuelCo", "Fuel", new BigDecimal("5.00"), POSTED_AT));
        TotalProductCashbackService service = new TotalProductCashbackService(cashbacks);

        assertThat(service.totalFor("Groceries").totalCashback()).isEqualByComparingTo("4.00");
    }

    @Test
    @DisplayName("reports the product name and the number of cashback payments counted")
    void reportsProductNameAndPaymentCount() {
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        cashbacks.save(new CashbackRecord("cust-001", "Market-A", "Groceries", new BigDecimal("2.40"), POSTED_AT));
        cashbacks.save(new CashbackRecord("cust-002", "Market-B", "Groceries", new BigDecimal("1.60"), POSTED_AT));
        cashbacks.save(new CashbackRecord("cust-003", "FuelCo", "Fuel", new BigDecimal("5.00"), POSTED_AT));
        TotalProductCashbackService service = new TotalProductCashbackService(cashbacks);

        ProductCashbackTotal total = service.totalFor("Groceries");

        assertThat(total.product()).isEqualTo("Groceries");
        assertThat(total.recordCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("expresses the total in whole cents (scale 2), even for a product that has paid no cashback")
    void expressesTheTotalInWholeCents() {
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        TotalProductCashbackService service = new TotalProductCashbackService(cashbacks);

        assertThat(service.totalFor("Travel").totalCashback().scale()).isEqualTo(2);
    }
}
