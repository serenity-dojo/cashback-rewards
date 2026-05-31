package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.TotalProductCashbackUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRepository;
import com.serenitydojo.cashback_rewards.domain.model.ProductCashbackTotal;
import org.springframework.stereotype.Service;

@Service
public class TotalProductCashbackService implements TotalProductCashbackUseCase {

    private final CashbackRepository cashbacks;

    public TotalProductCashbackService(CashbackRepository cashbacks) {
        this.cashbacks = cashbacks;
    }

    @Override
    public ProductCashbackTotal totalFor(String productCategory) {
        return new ProductCashbackTotal(
                productCategory,
                cashbacks.totalForProductCategory(productCategory),
                cashbacks.countForProductCategory(productCategory));
    }
}
