package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.adapter.out.persistence.InMemoryCashbackRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ListCustomerCashbackService")
class ListCustomerCashbackServiceTest {

    @Test
    @DisplayName("returns the cashback records previously stored for the customer")
    void returnsStoredCashbackRecordsForCustomer() {
        InMemoryCashbackRepository cashbacks = new InMemoryCashbackRepository();
        CashbackRecord record = new CashbackRecord("cust-001", "GreenGrocer", "Groceries", new BigDecimal("2.40"));
        cashbacks.save(record);
        ListCustomerCashbackService service = new ListCustomerCashbackService(cashbacks);

        assertThat(service.listFor("cust-001")).containsExactly(record);
    }
}
