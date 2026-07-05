package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "cashback_record")
class CashbackRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "product_category", nullable = false)
    private String productCategory;

    @Column(name = "cashback_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal cashbackAmount;

    @Column(name = "posted_at", nullable = false)
    private Instant postedAt;

    protected CashbackRecordEntity() {
        // required by JPA
    }

    CashbackRecordEntity(String customerId, String merchantName, String productCategory,
                         BigDecimal cashbackAmount, Instant postedAt) {
        this.customerId = customerId;
        this.merchantName = merchantName;
        this.productCategory = productCategory;
        this.cashbackAmount = cashbackAmount;
        this.postedAt = postedAt;
    }

    String getCustomerId() {
        return customerId;
    }

    String getMerchantName() {
        return merchantName;
    }

    String getProductCategory() {
        return productCategory;
    }

    BigDecimal getCashbackAmount() {
        return cashbackAmount;
    }

    Instant getPostedAt() {
        return postedAt;
    }
}
