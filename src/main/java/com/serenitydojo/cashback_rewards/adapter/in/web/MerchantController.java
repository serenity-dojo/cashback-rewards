package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final RegisterMerchantUseCase registerMerchant;

    public MerchantController(RegisterMerchantUseCase registerMerchant) {
        this.registerMerchant = registerMerchant;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody MerchantRequest request) {
        registerMerchant.register(new Merchant(request.name(), request.partner()));
    }

    public record MerchantRequest(
            @NotBlank String name,
            @NotNull Boolean partner) {
    }
}
