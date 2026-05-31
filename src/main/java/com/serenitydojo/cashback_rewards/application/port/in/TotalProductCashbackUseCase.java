package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.ProductCashbackTotal;

public interface TotalProductCashbackUseCase {
    ProductCashbackTotal totalFor(String productCategory);
}
