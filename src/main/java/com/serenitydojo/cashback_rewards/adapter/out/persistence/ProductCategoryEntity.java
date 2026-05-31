package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "product_category")
class ProductCategoryEntity {

    @Id
    @Column(name = "mcc", nullable = false)
    private String mcc;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cashback_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal cashbackRate;

    protected ProductCategoryEntity() {
        // required by JPA
    }

    ProductCategoryEntity(String mcc, String name, BigDecimal cashbackRate) {
        this.mcc = mcc;
        this.name = name;
        this.cashbackRate = cashbackRate;
    }

    String getMcc() {
        return mcc;
    }

    String getName() {
        return name;
    }

    BigDecimal getCashbackRate() {
        return cashbackRate;
    }
}
