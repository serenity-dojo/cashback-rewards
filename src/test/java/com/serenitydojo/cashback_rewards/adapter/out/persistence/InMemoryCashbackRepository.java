package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.CashbackRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory test double for {@link CashbackRepository}, used by the fast,
 * Spring-free application-layer unit tests. Production persistence is provided
 * by {@link JpaCashbackRepository}.
 */
public class InMemoryCashbackRepository implements CashbackRepository {

    private final ConcurrentMap<String, List<CashbackRecord>> byCustomer = new ConcurrentHashMap<>();

    @Override
    public void save(CashbackRecord record) {
        byCustomer.computeIfAbsent(record.customerId(), k -> new ArrayList<>()).add(record);
    }

    @Override
    public List<CashbackRecord> findByCustomerId(String customerId) {
        return List.copyOf(byCustomer.getOrDefault(customerId, List.of()));
    }

    @Override
    public BigDecimal totalForProductCategory(String productCategory) {
        return byCustomer.values().stream()
                .flatMap(List::stream)
                .filter(record -> record.productCategory().equals(productCategory))
                .map(CashbackRecord::cashbackAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public long countForProductCategory(String productCategory) {
        return byCustomer.values().stream()
                .flatMap(List::stream)
                .filter(record -> record.productCategory().equals(productCategory))
                .count();
    }
}
