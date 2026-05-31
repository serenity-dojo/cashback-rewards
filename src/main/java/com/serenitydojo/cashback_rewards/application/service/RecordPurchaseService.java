package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.RecordPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRepository;
import com.serenitydojo.cashback_rewards.application.port.out.CategoryRepository;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.model.ProductCategory;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class RecordPurchaseService implements RecordPurchaseUseCase {

    private final MerchantRepository merchants;
    private final CategoryRepository categories;
    private final CashbackRepository cashbacks;

    public RecordPurchaseService(MerchantRepository merchants,
                                 CategoryRepository categories,
                                 CashbackRepository cashbacks) {
        this.merchants = merchants;
        this.categories = categories;
        this.cashbacks = cashbacks;
    }

    @Override
    public void record(String customerId, String merchantName, String mcc, BigDecimal amount, Instant purchasedAt) {
        merchants.findByName(merchantName)
                .filter(Merchant::partner)
                .ifPresent(merchant -> {
                    ProductCategory category = categories.findByMcc(mcc)
                            .orElseGet(() -> ProductCategory.unmapped(mcc, categories.defaultRate()));
                    BigDecimal cashbackAmount = CashbackCalculator.calculate(amount, category.cashbackRate());
                    cashbacks.save(new CashbackRecord(customerId, merchantName, category.name(), cashbackAmount));
                });
    }
}
