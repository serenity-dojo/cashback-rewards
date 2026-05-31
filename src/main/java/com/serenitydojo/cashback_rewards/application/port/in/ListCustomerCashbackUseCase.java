package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;

import java.util.List;

public interface ListCustomerCashbackUseCase {
    List<CashbackRecord> listFor(String customerId);
}
