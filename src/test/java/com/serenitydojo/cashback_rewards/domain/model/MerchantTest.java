package com.serenitydojo.cashback_rewards.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Merchant")
class MerchantTest {

    @Test
    @DisplayName("carries a partner flag identifying whether the merchant is enrolled in the cashback program")
    void merchantsCarryAPartnerFlag() {
        assertThat(new Merchant("GreenGrocer", true).partner()).isTrue();
        assertThat(new Merchant("The Corner Café", false).partner()).isFalse();
    }
}
