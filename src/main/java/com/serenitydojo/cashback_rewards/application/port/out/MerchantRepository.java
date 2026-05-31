package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;

import java.util.Optional;

public interface MerchantRepository {
    void save(Merchant merchant);

    Optional<Merchant> findByName(String name);
}