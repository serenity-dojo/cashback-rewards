package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RecordPurchaseUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final RecordPurchaseUseCase recordPurchase;

    public PurchaseController(RecordPurchaseUseCase recordPurchase) {
        this.recordPurchase = recordPurchase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void record(@Valid @RequestBody PurchaseRequest request) {
        recordPurchase.record(request.customerId(), request.merchantName(), request.mcc(),
                request.amount(), request.purchasedAt());
    }

    public record PurchaseRequest(
            @NotBlank String customerId,
            @NotBlank String merchantName,
            @NotBlank String mcc,
            @NotNull BigDecimal amount,
            @NotNull Instant purchasedAt) {
    }
}
