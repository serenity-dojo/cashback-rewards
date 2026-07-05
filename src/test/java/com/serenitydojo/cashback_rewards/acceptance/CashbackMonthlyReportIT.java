package com.serenitydojo.cashback_rewards.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Cashback Monthly Report")
class CashbackMonthlyReportIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Rule: Must report cashback for a single calendar month in the member's local timezone")
    class ReportsASingleCalendarMonthInTheMembersLocalTimezone {

        @Test
        @DisplayName("The one where a member requests their report for March 2026 — it contains every cashback event " +
                "with a posting date between 1 March 00:00 and 31 March 23:59 in the member's local timezone")
        void includesOnlyEventsPostedWithinTheLocalCalendarMonth() throws Exception {
            // Member's local timezone is UTC+10 (Brisbane, no daylight saving),
            // so the March boundaries in UTC are 2026-02-28 14:00Z .. 2026-03-31 13:59Z.
            registerMember("mem-brisbane", "Australia/Brisbane");
            registerCategory("7001", "Groceries", "0.02");
            registerPartnerMerchant("MarchStart");
            registerPartnerMerchant("MarchEnd");
            registerPartnerMerchant("FebEnd");
            registerPartnerMerchant("AprilStart");

            // First instant of March, local time — belongs to March (its UTC date is 28 Feb).
            recordPurchaseAt("mem-brisbane", "MarchStart", "50.00", "7001", "2026-03-01T00:00:00+10:00");
            // Last minute of March, local time — belongs to March.
            recordPurchaseAt("mem-brisbane", "MarchEnd", "50.00", "7001", "2026-03-31T23:59:00+10:00");
            // Last minute of February, local time — belongs to February, excluded.
            recordPurchaseAt("mem-brisbane", "FebEnd", "50.00", "7001", "2026-02-28T23:59:00+10:00");
            // First instant of April, local time — belongs to April (its UTC date is 31 March), excluded.
            recordPurchaseAt("mem-brisbane", "AprilStart", "50.00", "7001", "2026-04-01T00:00:00+10:00");

            mockMvc.perform(get("/api/members/{memberId}/cashback-report", "mem-brisbane")
                            .param("month", "2026-03"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.entries.length()").value(2))
                    .andExpect(jsonPath("$.entries[*].merchantName",
                            containsInAnyOrder("MarchStart", "MarchEnd")));
        }

        @Test
        @DisplayName("The one where a member ahead of UTC has a transaction that posts at 11pm local on 31 March " +
                "(already 1 April UTC) — it appears in the March report, not April")
        void filesTheEventByTheMembersLocalMonthNotUtc() throws Exception {
            // Member's local timezone is UTC-11 (Pago Pago), so 11pm local on 31 March
            // is 2026-04-01 10:00Z — already 1 April in UTC.
            registerMember("mem-pago", "Pacific/Pago_Pago");
            registerCategory("7002", "Groceries", "0.02");
            registerPartnerMerchant("LateMarchLocal");

            recordPurchaseAt("mem-pago", "LateMarchLocal", "50.00", "7002", "2026-03-31T23:00:00-11:00");

            mockMvc.perform(get("/api/members/{memberId}/cashback-report", "mem-pago")
                            .param("month", "2026-03"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.entries.length()").value(1))
                    .andExpect(jsonPath("$.entries[*].merchantName",
                            containsInAnyOrder("LateMarchLocal")));

            mockMvc.perform(get("/api/members/{memberId}/cashback-report", "mem-pago")
                            .param("month", "2026-04"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.entries.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Rule: Must show the monthly total as the net sum of all entries")
    class ShowsTheMonthlyTotalAsTheNetSumOfEntries {

        @Test
        @DisplayName("The one where a member has had no qualifying activity in the requested month — " +
                "an empty list and a total of $0.00, not an error")
        void returnsEmptyReportWithZeroTotalForAMonthWithNoActivity() throws Exception {
            registerMember("mem-quiet", "Australia/Brisbane");

            String body = mockMvc.perform(get("/api/members/{memberId}/cashback-report", "mem-quiet")
                            .param("month", "2026-03"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            Report report = objectMapper.readValue(body, Report.class);

            assertThat(report.entries()).isEmpty();
            assertThat(report.total()).isEqualByComparingTo("0.00");
            assertThat(report.total().scale()).isEqualTo(2);
        }
    }

    record Report(List<Entry> entries, BigDecimal total) {
    }

    record Entry(String merchantName, BigDecimal cashbackAmount) {
    }

    private void registerMember(String memberId, String timeZone) throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "memberId": "%s",
                                  "timeZone": "%s"
                                }
                                """.formatted(memberId, timeZone)))
                .andExpect(status().isCreated());
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

    private void recordPurchaseAt(String customerId, String merchantName, String amount,
                                  String mcc, String purchasedAt) throws Exception {
        mockMvc.perform(post("/api/purchases")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "merchantName": "%s",
                                  "amount": "%s",
                                  "mcc": "%s",
                                  "purchasedAt": "%s"
                                }
                                """.formatted(customerId, merchantName, amount, mcc, purchasedAt)))
                .andExpect(status().is2xxSuccessful());
    }
}
