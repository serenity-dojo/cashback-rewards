package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.MonthlyCashbackReport;

import java.time.YearMonth;

public interface MonthlyCashbackReportUseCase {
    MonthlyCashbackReport reportFor(String memberId, YearMonth month);
}
