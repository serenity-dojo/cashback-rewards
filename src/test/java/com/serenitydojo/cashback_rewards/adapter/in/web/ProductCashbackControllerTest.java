package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.TotalProductCashbackUseCase;
import com.serenitydojo.cashback_rewards.domain.model.ProductCashbackTotal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductCashbackController.class)
@DisplayName("ProductCashbackController")
class ProductCashbackControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TotalProductCashbackUseCase totalCashback;

    @Test
    @DisplayName("GET /api/products/{productCategory}/cashback-total returns the product, total cashback and record count as JSON")
    void returnsProductTotalAsJson() throws Exception {
        when(totalCashback.totalFor("Groceries"))
                .thenReturn(new ProductCashbackTotal("Groceries", new BigDecimal("4.00"), 2L));

        mockMvc.perform(get("/api/products/{productCategory}/cashback-total", "Groceries"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"product": "Groceries", "totalCashback": 4.00, "recordCount": 2}
                        """));
    }
}
