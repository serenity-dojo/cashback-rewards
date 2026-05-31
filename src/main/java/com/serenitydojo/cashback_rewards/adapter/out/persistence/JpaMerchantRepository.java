package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;

@Repository
class JpaMerchantRepository implements MerchantRepository {

    private final MerchantJpaRepository merchants;

    JpaMerchantRepository(MerchantJpaRepository merchants) {
        this.merchants = merchants;
    }

    @Override
    public void save(Merchant merchant) {
        merchants.save(new MerchantEntity(normalize(merchant.name()), merchant.name(), merchant.partner()));
    }

    @Override
    public Optional<Merchant> findByName(String name) {
        return merchants.findById(normalize(name)).map(JpaMerchantRepository::toDomain);
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }

    private static Merchant toDomain(MerchantEntity entity) {
        return new Merchant(entity.getName(), entity.isPartner());
    }
}
