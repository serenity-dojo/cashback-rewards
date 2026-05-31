package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.ManageProductCategoriesUseCase;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@DisplayName("CategoryController")
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ManageProductCategoriesUseCase manageCategories;

    @Test
    @DisplayName("POST /api/categories registers the product category")
    void registersProductCategory() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "mcc": "5411",
                                  "name": "Groceries",
                                  "cashbackRate": "0.02"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(manageCategories).register(new ProductCategory("5411", "Groceries", new BigDecimal("0.02")));
    }

    @Test
    @DisplayName("PUT /api/categories/default-rate configures the fallback rate for unmapped MCCs")
    void configuresDefaultRate() throws Exception {
        mockMvc.perform(put("/api/categories/default-rate")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "cashbackRate": "0.005"
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(manageCategories).configureDefaultRate(new BigDecimal("0.005"));
    }
}