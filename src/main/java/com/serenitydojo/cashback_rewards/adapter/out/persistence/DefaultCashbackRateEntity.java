package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * The default cashback rate is a single configured value, modelled as one row
 * with a fixed primary key so that saving it again overwrites the previous value.
 */
@Entity
@Table(name = "default_cashback_rate")
class DefaultCashbackRateEntity {

    static final int SINGLETON_ID = 1;

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal rate;

    protected DefaultCashbackRateEntity() {
        // required by JPA
    }

    DefaultCashbackRateEntity(BigDecimal rate) {
        this.id = SINGLETON_ID;
        this.rate = rate;
    }

    BigDecimal getRate() {
        return rate;
    }
}
