package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.adapter.out.persistence.InMemoryCashbackRepository;
import com.serenitydojo.cashback_rewards.adapter.out.persistence.InMemoryCategoryRepository;
import com.serenitydojo.cashback_rewards.adapter.out.persistence.InMemoryMerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecordPurchaseService")
class RecordPurchaseServiceTest {

    @Test
    @DisplayName("silently ignores a purchase at an unregistered (non-partner) merchant — no cashback record is created")
    void ignoresPurchasesAtNonPartnerMerchants() {
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        RecordPurchaseService service = new RecordPurchaseService(merchants, categories, cashbacks);

        service.record("cust-002", "The Corner Café", "5411", new BigDecimal("80.00"),
                Instant.parse("2026-05-01T10:00:00Z"));

        assertThat(cashbacks.findByCustomerId("cust-002")).isEmpty();
    }

    @Test
    @DisplayName("computes cashback using the rate configured for the transaction's MCC")
    void usesCategoryRateForMatchingMcc() {
        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        categories.save(new ProductCategory("5411", "Groceries", new BigDecimal("0.02")));
        merchants.save(new Merchant("GreenGrocer", true));
        RecordPurchaseService service = new RecordPurchaseService(merchants, categories, cashbacks);

        service.record("cust-001", "GreenGrocer", "5411", new BigDecimal("80.00"),
                Instant.parse("2026-05-01T10:00:00Z"));

        assertThat(cashbacks.findByCustomerId("cust-001"))
                .singleElement()
                .satisfies(rec -> assertThat(rec.cashbackAmount()).isEqualByComparingTo("1.60"));
    }

    @Test
    @DisplayName("records the name of the product category that earned the cashback")
    void cashbackRecordCarriesTheProductCategoryName() {
        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        categories.save(new ProductCategory("5411", "Groceries", new BigDecimal("0.02")));
        merchants.save(new Merchant("GreenGrocer", true));
        RecordPurchaseService service = new RecordPurchaseService(merchants, categories, cashbacks);

        service.record("cust-001", "GreenGrocer", "5411", new BigDecimal("80.00"),
                Instant.parse("2026-05-01T10:00:00Z"));

        assertThat(cashbacks.findByCustomerId("cust-001"))
                .singleElement()
                .satisfies(rec -> assertThat(rec.productCategory()).isEqualTo("Groceries"));
    }

    @Test
    @DisplayName("does not award cashback for a purchase at a registered non-partner merchant")
    void nonPartnerMerchantsEarnNoCashback() {
        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        categories.save(new ProductCategory("5411", "Groceries", new BigDecimal("0.02")));
        merchants.save(new Merchant("The Corner Café", false));
        RecordPurchaseService service = new RecordPurchaseService(merchants, categories, cashbacks);

        service.record("cust-003", "The Corner Café", "5411", new BigDecimal("80.00"),
                Instant.parse("2026-05-01T10:00:00Z"));

        assertThat(cashbacks.findByCustomerId("cust-003")).isEmpty();
    }

    @Test
    @DisplayName("falls back to the configured default rate and the \"Other\" category for an unmapped MCC")
    void usesDefaultRateForUnmappedMcc() {
        InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        categories.saveDefaultRate(new BigDecimal("0.005"));
        merchants.save(new Merchant("Pharmacy", true));
        RecordPurchaseService service = new RecordPurchaseService(merchants, categories, cashbacks);

        service.record("cust-004", "Pharmacy", "5912", new BigDecimal("100.00"),
                Instant.parse("2026-05-01T10:00:00Z"));

        assertThat(cashbacks.findByCustomerId("cust-004"))
                .singleElement()
                .satisfies(rec -> {
                    assertThat(rec.cashbackAmount()).isEqualByComparingTo("0.50");
                    assertThat(rec.productCategory()).isEqualTo("Other");
                });
    }
}
