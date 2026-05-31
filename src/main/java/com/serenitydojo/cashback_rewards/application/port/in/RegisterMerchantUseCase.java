package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;

public interface RegisterMerchantUseCase {
    void register(Merchant merchant);
}
