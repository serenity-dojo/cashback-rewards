package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.ListCustomerCashbackUseCase;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CashbackController.class)
@DisplayName("CashbackController")
class CashbackControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ListCustomerCashbackUseCase listCashback;

    @Test
    @DisplayName("GET /api/customers/{customerId}/cashback returns the customer's cashback records as JSON")
    void returnsCustomerCashbackAsJson() throws Exception {
        when(listCashback.listFor("cust-001")).thenReturn(List.of(
                new CashbackRecord("cust-001", "GreenGrocer", "Groceries", new BigDecimal("2.40"))));

        mockMvc.perform(get("/api/customers/{customerId}/cashback", "cust-001"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"merchantName": "GreenGrocer", "productCategory": "Groceries", "cashbackAmount": 2.40}]
                        """));
    }
}
