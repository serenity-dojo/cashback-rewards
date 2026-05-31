package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaMerchantRepository.class)
@DisplayName("JpaMerchantRepository")
class JpaMerchantRepositoryTest {

    @Autowired
    JpaMerchantRepository repository;

    @Test
    @DisplayName("returns a previously saved merchant when looked up by name")
    void findsSavedMerchantByName() {
        Merchant greenGrocer = new Merchant("GreenGrocer", true);

        repository.save(greenGrocer);

        assertThat(repository.findByName("GreenGrocer")).contains(greenGrocer);
    }

    @ParameterizedTest(name = "lookup with \"{0}\" finds the merchant saved as \"GreenGrocer\"")
    @ValueSource(strings = {"greengrocer", "GREENGROCER", " GreenGrocer ", "  greengrocer  "})
    @DisplayName("matches merchant names case-insensitively and ignores surrounding whitespace")
    void findsMerchantIgnoringCaseAndWhitespace(String lookup) {
        repository.save(new Merchant("GreenGrocer", true));

        assertThat(repository.findByName(lookup))
                .contains(new Merchant("GreenGrocer", true));
    }
}
