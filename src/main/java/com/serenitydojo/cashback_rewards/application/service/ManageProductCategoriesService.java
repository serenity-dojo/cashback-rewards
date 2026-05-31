package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.ManageProductCategoriesUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CategoryRepository;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ManageProductCategoriesService implements ManageProductCategoriesUseCase {

    private final CategoryRepository categories;

    public ManageProductCategoriesService(CategoryRepository categories) {
        this.categories = categories;
    }

    public void register(ProductCategory category) {
        categories.save(category);
    }

    public void configureDefaultRate(BigDecimal rate) {
        categories.saveDefaultRate(rate);
    }
}