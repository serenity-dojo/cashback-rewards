package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.TotalProductCashbackUseCase;
import com.serenitydojo.cashback_rewards.domain.model.ProductCashbackTotal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductCashbackController {

    private final TotalProductCashbackUseCase totalCashback;

    public ProductCashbackController(TotalProductCashbackUseCase totalCashback) {
        this.totalCashback = totalCashback;
    }

    @GetMapping("/{productCategory}/cashback-total")
    public CashbackTotalResponse totalFor(@PathVariable String productCategory) {
        ProductCashbackTotal total = totalCashback.totalFor(productCategory);
        return new CashbackTotalResponse(total.product(), total.totalCashback(), total.recordCount());
    }

    public record CashbackTotalResponse(String product, BigDecimal totalCashback, long recordCount) {
    }
}
