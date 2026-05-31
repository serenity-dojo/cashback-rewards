package com.serenitydojo.cashback_rewards.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Total Cashback Paid per Product")
class TotalCashbackPerProductIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void startFromACleanLedger() {
        // The product total aggregates across every customer, so each example
        // must start from a clean ledger to be deterministic in the shared context.
        jdbc.execute("DELETE FROM cashback_record");
        jdbc.execute("DELETE FROM merchant");
        jdbc.execute("DELETE FROM product_category");
        jdbc.execute("DELETE FROM default_cashback_rate");
    }

    @Nested
    @DisplayName("Rule: Must total the cashback paid for a product as the sum of every cashback record for that product, across all customers")
    class TotalsAcrossAllCustomers {

        @Test
        @DisplayName("The one where Groceries cashback earned by two customers totals $4.00 and Fuel cashback is excluded")
        void sumsCashbackForTheProductAcrossCustomersAndExcludesOtherProducts() throws Exception {
            registerCategory("5411", "Groceries", "0.02");
            registerCategory("5541", "Fuel", "0.01");
            registerPartnerMerchant("Market-A");
            registerPartnerMerchant("Market-B");
            registerPartnerMerchant("FuelCo");

            recordPurchase("cust-001", "Market-A", "120.00", "5411"); // $2.40 Groceries
            recordPurchase("cust-002", "Market-B", "80.00", "5411");  // $1.60 Groceries
            recordPurchase("cust-003", "FuelCo", "500.00", "5541");   // $5.00 Fuel (excluded)

            mockMvc.perform(get("/api/products/{productCategory}/cashback-total", "Groceries"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                            {"totalCashback": 4.00}
                            """));
        }
    }

    @Nested
    @DisplayName("Rule: Must return a total of $0.00 for a product with no cashback records")
    class ReturnsZeroForProductsWithNoCashback {

        @Test
        @DisplayName("The one where no purchase has ever earned cashback in Travel — the total is $0.00, not an error")
        void returnsZeroForAProductThatHasEarnedNoCashback() throws Exception {
            mockMvc.perform(get("/api/products/{productCategory}/cashback-total", "Travel"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                            {"totalCashback": 0.00}
                            """));
        }
    }

    @Nested
    @DisplayName("Rule: Must report the product, the total cashback paid, and the number of cashback payments counted")
    class ReportsProductTotalAndPaymentCount {

        @Test
        @DisplayName("The one where Groceries has two payments totalling $4.00 — product Groceries, total $4.00, count 2")
        void reportsProductTotalAndPaymentCount() throws Exception {
            registerCategory("5411", "Groceries", "0.02");
            registerPartnerMerchant("Market-A");
            registerPartnerMerchant("Market-B");
            recordPurchase("cust-001", "Market-A", "120.00", "5411"); // $2.40
            recordPurchase("cust-002", "Market-B", "80.00", "5411");  // $1.60

            mockMvc.perform(get("/api/products/{productCategory}/cashback-total", "Groceries"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                            {"product": "Groceries", "totalCashback": 4.00, "recordCount": 2}
                            """));
        }

        @Test
        @DisplayName("The one where Travel has no payments — product Travel, total $0.00, count 0")
        void reportsZeroCountForAProductWithNoPayments() throws Exception {
            mockMvc.perform(get("/api/products/{productCategory}/cashback-total", "Travel"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                            {"product": "Travel", "totalCashback": 0.00, "recordCount": 0}
                            """));
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
}
