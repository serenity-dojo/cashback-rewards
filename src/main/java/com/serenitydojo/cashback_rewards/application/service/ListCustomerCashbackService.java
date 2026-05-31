package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.ListCustomerCashbackUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCustomerCashbackService implements ListCustomerCashbackUseCase {

    private final CashbackRepository cashbacks;

    public ListCustomerCashbackService(CashbackRepository cashbacks) {
        this.cashbacks = cashbacks;
    }

    @Override
    public List<CashbackRecord> listFor(String customerId) {
        return cashbacks.findByCustomerId(customerId);
    }
}
