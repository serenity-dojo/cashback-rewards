package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.CashbackRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
class JpaCashbackRepository implements CashbackRepository {

    private final CashbackJpaRepository cashbacks;

    JpaCashbackRepository(CashbackJpaRepository cashbacks) {
        this.cashbacks = cashbacks;
    }

    @Override
    public void save(CashbackRecord record) {
        cashbacks.save(new CashbackRecordEntity(
                record.customerId(), record.merchantName(), record.productCategory(), record.cashbackAmount()));
    }

    @Override
    public List<CashbackRecord> findByCustomerId(String customerId) {
        return cashbacks.findByCustomerId(customerId).stream()
                .map(JpaCashbackRepository::toDomain)
                .toList();
    }

    @Override
    public BigDecimal totalForProductCategory(String productCategory) {
        return cashbacks.sumByProductCategory(productCategory);
    }

    @Override
    public long countForProductCategory(String productCategory) {
        return cashbacks.countByProductCategory(productCategory);
    }

    private static CashbackRecord toDomain(CashbackRecordEntity entity) {
        return new CashbackRecord(
                entity.getCustomerId(), entity.getMerchantName(),
                entity.getProductCategory(), entity.getCashbackAmount());
    }
}
