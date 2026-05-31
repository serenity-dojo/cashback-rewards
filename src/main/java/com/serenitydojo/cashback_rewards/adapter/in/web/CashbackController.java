package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.ListCustomerCashbackUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CashbackController {

    private final ListCustomerCashbackUseCase listCashback;

    public CashbackController(ListCustomerCashbackUseCase listCashback) {
        this.listCashback = listCashback;
    }

    @GetMapping("/{customerId}/cashback")
    public List<CashbackResponse> listFor(@PathVariable String customerId) {
        return listCashback.listFor(customerId).stream()
                .map(record -> new CashbackResponse(record.merchantName(), record.productCategory(), record.cashbackAmount()))
                .toList();
    }

    public record CashbackResponse(String merchantName, String productCategory, BigDecimal cashbackAmount) {
    }
}
