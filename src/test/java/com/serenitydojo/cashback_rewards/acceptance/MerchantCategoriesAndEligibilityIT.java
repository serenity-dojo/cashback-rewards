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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Merchant Categories & Eligibility")
class MerchantCategoriesAndEligibilityIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Rule: Should award cashback at the rate configured for the product category")
    class AwardsCashbackAtTheConfiguredCategoryRate {

        @ParameterizedTest(name = "The one where a $100.00 purchase in {1} (MCC {0}) earns ${3}")
        @CsvSource(textBlock = """
                5411, Groceries, 0.02, 2.00
                5541, Fuel,      0.01, 1.00
                """)
        void awardsCashbackAtTheConfiguredCategoryRate(String mcc,
                                                       String categoryName,
                                                       String categoryRate,
                                                       String expectedCashback) throws Exception {
            String customerId = "cust-cat-" + mcc;
            String merchantName = "Merchant-" + categoryName;

            registerCategory(mcc, categoryName, categoryRate);
            registerPartnerMerchant(merchantName);

            recordPurchase(customerId, merchantName, "100.00", mcc);

            List<CashbackRecord> records = cashbackFor(customerId);

            assertThat(records).hasSize(1);
            assertThat(records.getFirst().merchantName()).isEqualTo(merchantName);
            assertThat(records.getFirst().productCategory()).isEqualTo(categoryName);
            assertThat(records.getFirst().cashbackAmount()).isEqualByComparingTo(expectedCashback);
        }

        @Test
        @DisplayName("The one where a $100.00 purchase with an unmapped MCC (5912 Pharmacy) falls back to the $0.50 default")
        void fallsBackToTheDefaultRateForAnUnmappedMcc() throws Exception {
            configureDefaultRate("0.005");
            registerPartnerMerchant("City Pharmacy");

            recordPurchase("cust-cat-default", "City Pharmacy", "100.00", "5912");

            List<CashbackRecord> records = cashbackFor("cust-cat-default");

            assertThat(records).hasSize(1);
            assertThat(records.getFirst().productCategory()).isEqualTo("Other");
            assertThat(records.getFirst().cashbackAmount()).isEqualByComparingTo("0.50");
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
        mockMvc.perform(post("/api/merchants")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "partner": true
                                }
                                """.formatted(name)))
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

    private List<CashbackRecord> cashbackFor(String customerId) throws Exception {
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
