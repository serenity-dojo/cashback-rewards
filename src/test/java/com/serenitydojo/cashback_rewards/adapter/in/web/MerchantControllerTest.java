package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MerchantController.class)
@DisplayName("MerchantController")
class MerchantControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RegisterMerchantUseCase registerMerchant;

    @Test
    @DisplayName("POST /api/merchants registers the merchant with its partner flag")
    void registersMerchantWithPartnerFlag() throws Exception {
        mockMvc.perform(post("/api/merchants")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "GreenGrocer",
                                  "partner": true
                                }
                                """))
                .andExpect(status().isCreated());

        verify(registerMerchant).register(new Merchant("GreenGrocer", true));
    }
}
