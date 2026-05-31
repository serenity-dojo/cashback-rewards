package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.ManageProductCategoriesUseCase;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ManageProductCategoriesUseCase manageCategories;

    public CategoryController(ManageProductCategoriesUseCase manageCategories) {
        this.manageCategories = manageCategories;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody CategoryRequest request) {
        manageCategories.register(new ProductCategory(request.mcc(), request.name(), request.cashbackRate()));
    }

    @PutMapping("/default-rate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void configureDefaultRate(@Valid @RequestBody DefaultRateRequest request) {
        manageCategories.configureDefaultRate(request.cashbackRate());
    }

    public record CategoryRequest(
            @NotBlank String mcc,
            @NotBlank String name,
            @NotNull BigDecimal cashbackRate) {
    }

    public record DefaultRateRequest(
            @NotNull BigDecimal cashbackRate) {
    }
}