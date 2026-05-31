package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

interface CashbackJpaRepository extends JpaRepository<CashbackRecordEntity, Long> {

    List<CashbackRecordEntity> findByCustomerId(String customerId);

    @Query("""
            select coalesce(sum(c.cashbackAmount), 0)
            from CashbackRecordEntity c
            where c.productCategory = :productCategory
            """)
    BigDecimal sumByProductCategory(@Param("productCategory") String productCategory);

    long countByProductCategory(String productCategory);
}
