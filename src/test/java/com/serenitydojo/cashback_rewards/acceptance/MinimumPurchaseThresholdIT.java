package com.serenitydojo.cashback_rewards.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Minimum Purchase Threshold")
class MinimumPurchaseThresholdIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Rule: Must not award cashback on purchases below the minimum threshold ($1.00)")
    class MustNotAwardCashbackBelowTheMinimumThreshold {

        @ParameterizedTest(name = "The one where a ${0} purchase in {2} earns ${4}")
        @CsvSource(textBlock = """
                 0.50, 5411, Groceries, 0.02, 0.00
                 0.99, 5541, Fuel,      0.01, 0.00
                 1.00, 5411, Groceries, 0.02, 0.02
                25.00, 5411, Groceries, 0.02, 0.50
                """)
        void doesNotAwardCashbackBelowTheThreshold(String amount,
                                                   String mcc,
                                                   String categoryName,
                                                   String categoryRate,
                                                   String expectedCashback) throws Exception {
            String customerId = "cust-threshold-" + amount;
            String merchantName = "Merchant-" + categoryName + "-" + amount;

            registerCategory(mcc, categoryName, categoryRate);
            registerPartnerMerchant(merchantName);

            recordPurchase(customerId, merchantName, amount, mcc);

            assertThat(totalCashbackFor(customerId)).isEqualByComparingTo(expectedCashback);
        }
    }

    @Nested
    @DisplayName("Rule: Should apply the threshold to the purchase amount, not the resulting cashback amount")
    class AppliesTheThresholdToThePurchaseAmount {

        @Test
        @DisplayName("The one where a $0.75 purchase at 2% earns $0.00")
        void belowThresholdEarnsNothingRegardlessOfRate() throws Exception {
            registerCategory("5411", "Groceries", "0.02");
            registerPartnerMerchant("Tiny Grocer");

            recordPurchase("cust-rule2-below", "Tiny Grocer", "0.75", "5411");

            assertThat(totalCashbackFor("cust-rule2-below")).isEqualByComparingTo("0.00");
        }

        @Test
        @DisplayName("The one where a $5.00 purchase at 0.5% earns $0.02")
        void aboveThresholdEarnsCashbackEvenWhenTheRewardIsTiny() throws Exception {
            configureDefaultRate("0.005");
            registerPartnerMerchant("Penny Saver");

            recordPurchase("cust-rule2-above", "Penny Saver", "5.00", "9999");

            assertThat(totalCashbackFor("cust-rule2-above")).isEqualByComparingTo("0.02");
        }
    }

    @Nested
    @DisplayName("Rule: Must not create a cashback record for below-threshold purchases")
    class MustNotCreateACashbackRecordBelowTheThreshold {

        @Test
        @DisplayName("The one where a $0.50 purchase at a partner merchant results in no cashback record being stored")
        void belowThresholdStoresNoRecord() throws Exception {
            registerCategory("5411", "Groceries", "0.02");
            registerPartnerMerchant("Below Mart");

            recordPurchase("cust-rule3-below", "Below Mart", "0.50", "5411");

            assertThat(cashbackRecordsFor("cust-rule3-below")).isEmpty();
        }

        @Test
        @DisplayName("The one where a $1.00 purchase at a partner merchant creates a cashback record with the calculated amount")
        void atThresholdStoresARecordWithTheCalculatedAmount() throws Exception {
            registerCategory("5411", "Groceries", "0.02");
            registerPartnerMerchant("At Mart");

            recordPurchase("cust-rule3-at", "At Mart", "1.00", "5411");

            assertThat(cashbackRecordsFor("cust-rule3-at"))
                    .singleElement()
                    .satisfies(rec -> assertThat(rec.cashbackAmount()).isEqualByComparingTo("0.02"));
        }
    }

    @Nested
    @DisplayName("Rule: Should treat the threshold check as independent of other eligibility rules")
    class ThresholdIsIndependentOfOtherEligibilityRules {

        @Test
        @DisplayName("The one where a $0.80 purchase at a partner merchant earns no cashback — passes partner check but fails threshold check")
        void belowThresholdAtPartnerMerchantEarnsNothing() throws Exception {
            registerCategory("5411", "Groceries", "0.02");
            registerMerchant("Partner Below", true);

            recordPurchase("cust-rule4-partner", "Partner Below", "0.80", "5411");

            assertThat(cashbackRecordsFor("cust-rule4-partner")).isEmpty();
        }

        @Test
        @DisplayName("The one where a $0.50 purchase at a non-partner merchant earns no cashback — fails partner check before threshold is even relevant")
        void belowThresholdAtNonPartnerMerchantEarnsNothing() throws Exception {
            registerCategory("5411", "Groceries", "0.02");
            registerMerchant("NonPartner Below", false);

            recordPurchase("cust-rule4-nonpartner", "NonPartner Below", "0.50", "5411");

            assertThat(cashbackRecordsFor("cust-rule4-nonpartner")).isEmpty();
        }
    }

    private void registerCategory(String mcc, String name, String cashbackRate) throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "mcc": "%s",
                                  "name": "%s",
                                  "cashbackRate": "%s"
                                }
                                """.formatted(mcc, name, cashbackRate)))
                .andExpect(status().isCreated());
    }

    private void configureDefaultRate(String cashbackRate) throws Exception {
        mockMvc.perform(put("/api/categories/default-rate")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "cashbackRate": "%s"
                                }
                                """.formatted(cashbackRate)))
                .andExpect(status().is2xxSuccessful());
    }

    private void registerPartnerMerchant(String name) throws Exception {
        registerMerchant(name, true);
    }

    private void registerMerchant(String name, boolean partner) throws Exception {
        mockMvc.perform(post("/api/merchants")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "partner": %s
                                }
                                """.formatted(name, partner)))
                .andExpect(status().isCreated());
    }

    private void recordPurchase(String customerId, String merchantName, String amount, String mcc) throws Exception {
        mockMvc.perform(post("/api/purchases")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "merchantName": "%s",
                                  "amount": "%s",
                                  "mcc": "%s",
                                  "purchasedAt": "2026-05-01T10:00:00Z"
                                }
                                """.formatted(customerId, merchantName, amount, mcc)))
                .andExpect(status().is2xxSuccessful());
    }

    private BigDecimal totalCashbackFor(String customerId) throws Exception {
        return cashbackRecordsFor(customerId).stream()
                .map(CashbackRecord::cashbackAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.DOWN);
    }

    private List<CashbackRecord> cashbackRecordsFor(String customerId) throws Exception {
        String body = mockMvc.perform(get("/api/customers/{customerId}/cashback", customerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, new TypeReference<>() {
        });
    }

    record CashbackRecord(String merchantName, String productCategory, BigDecimal cashbackAmount) {
    }
}
