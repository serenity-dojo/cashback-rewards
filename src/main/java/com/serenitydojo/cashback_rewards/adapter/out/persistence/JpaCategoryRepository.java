package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.CategoryRepository;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
class JpaCategoryRepository implements CategoryRepository {

    private final CategoryJpaRepository categories;
    private final DefaultCashbackRateJpaRepository defaultRates;

    JpaCategoryRepository(CategoryJpaRepository categories, DefaultCashbackRateJpaRepository defaultRates) {
        this.categories = categories;
        this.defaultRates = defaultRates;
    }

    @Override
    public void save(ProductCategory category) {
        categories.save(new ProductCategoryEntity(category.mcc(), category.name(), category.cashbackRate()));
    }

    @Override
    public Optional<ProductCategory> findByMcc(String mcc) {
        return categories.findById(mcc).map(JpaCategoryRepository::toDomain);
    }

    @Override
    public void saveDefaultRate(BigDecimal rate) {
        defaultRates.save(new DefaultCashbackRateEntity(rate));
    }

    @Override
    public BigDecimal defaultRate() {
        return defaultRates.findById(DefaultCashbackRateEntity.SINGLETON_ID)
                .map(DefaultCashbackRateEntity::getRate)
                .orElse(null);
    }

    private static ProductCategory toDomain(ProductCategoryEntity entity) {
        return new ProductCategory(entity.getMcc(), entity.getName(), entity.getCashbackRate());
    }
}
