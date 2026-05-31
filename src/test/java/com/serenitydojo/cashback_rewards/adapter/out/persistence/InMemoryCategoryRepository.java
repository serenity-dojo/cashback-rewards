package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.CategoryRepository;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory test double for {@link CategoryRepository}, used by the fast,
 * Spring-free application-layer unit tests. Production persistence is provided
 * by {@link JpaCategoryRepository}.
 */
public class InMemoryCategoryRepository implements CategoryRepository {

    private final ConcurrentMap<String, ProductCategory> categories = new ConcurrentHashMap<>();
    private BigDecimal defaultRate;

    public void save(ProductCategory category) {
        categories.put(category.mcc(), category);
    }

    public Optional<ProductCategory> findByMcc(String mcc) {
        return Optional.ofNullable(categories.get(mcc));
    }

    public void saveDefaultRate(BigDecimal rate) {
        this.defaultRate = rate;
    }

    public BigDecimal defaultRate() {
        return defaultRate;
    }
}
