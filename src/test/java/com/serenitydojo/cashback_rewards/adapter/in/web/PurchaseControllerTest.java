package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RecordPurchaseUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
@DisplayName("PurchaseController")
class PurchaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RecordPurchaseUseCase recordPurchase;

    @Test
    @DisplayName("POST /api/purchases passes customer, merchant, mcc, amount and purchasedAt from the request body to the use case and returns 201")
    void passesPurchaseFieldsFromRequestBodyToUseCase() throws Exception {
        mockMvc.perform(post("/api/purchases")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "cust-001",
                                  "merchantName": "GreenGrocer",
                                  "mcc": "5411",
                                  "amount": "80.00",
                                  "purchasedAt": "2026-05-01T14:00:01Z"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(recordPurchase).record("cust-001", "GreenGrocer", "5411", new BigDecimal("80.00"),
                Instant.parse("2026-05-01T14:00:01Z"));
    }
}
