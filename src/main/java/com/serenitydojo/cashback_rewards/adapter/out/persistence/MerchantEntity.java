package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "merchant")
class MerchantEntity {

    @Id
    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "partner", nullable = false)
    private boolean partner;

    protected MerchantEntity() {
        // required by JPA
    }

    MerchantEntity(String normalizedName, String name, boolean partner) {
        this.normalizedName = normalizedName;
        this.name = name;
        this.partner = partner;
    }

    String getName() {
        return name;
    }

    boolean isPartner() {
        return partner;
    }
}
