package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.adapter.out.persistence.InMemoryMerchantRepository;
import com.serenitydojo.cashback_rewards.domain.exception.MerchantAlreadyRegisteredException;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RegisterMerchantService")
class RegisterMerchantServiceTest {

    @Test
    @DisplayName("makes a registered merchant retrievable from the repository by name")
    void registeredMerchantBecomesRetrievable() {
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        RegisterMerchantService service = new RegisterMerchantService(merchants);
        Merchant greenGrocer = new Merchant("GreenGrocer", true);

        service.register(greenGrocer);

        assertThat(merchants.findByName("GreenGrocer")).contains(greenGrocer);
    }

    @Test
    @DisplayName("rejects a second registration that reuses an existing merchant name")
    void rejectsDuplicateMerchantName() {
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        RegisterMerchantService service = new RegisterMerchantService(merchants);
        service.register(new Merchant("GreenGrocer", true));

        assertThatThrownBy(() ->
                service.register(new Merchant("GreenGrocer", true)))
                .isInstanceOf(MerchantAlreadyRegisteredException.class)
                .hasMessageContaining("GreenGrocer");
    }

    @ParameterizedTest(name = "\"{0}\" is rejected as a duplicate of GreenGrocer")
    @ValueSource(strings = {"greengrocer", "GREENGROCER", " GreenGrocer ", "  greengrocer  "})
    @DisplayName("treats duplicate merchant names case-insensitively and trims surrounding whitespace")
    void rejectsCaseAndWhitespaceVariantsAsDuplicates(String duplicate) {
        InMemoryMerchantRepository merchants = new InMemoryMerchantRepository();
        RegisterMerchantService service = new RegisterMerchantService(merchants);
        service.register(new Merchant("GreenGrocer", true));

        assertThatThrownBy(() ->
                service.register(new Merchant(duplicate, true)))
                .isInstanceOf(MerchantAlreadyRegisteredException.class);
    }
}
