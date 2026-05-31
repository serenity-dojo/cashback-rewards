package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory test double for {@link MerchantRepository}, used by the fast,
 * Spring-free application-layer unit tests. Production persistence is provided
 * by {@link JpaMerchantRepository}.
 */
public class InMemoryMerchantRepository implements MerchantRepository {

    private final ConcurrentMap<String, Merchant> merchants = new ConcurrentHashMap<>();

    @Override
    public void save(Merchant merchant) {
        merchants.put(normalize(merchant.name()), merchant);
    }

    @Override
    public Optional<Merchant> findByName(String name) {
        return Optional.ofNullable(merchants.get(normalize(name)));
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
