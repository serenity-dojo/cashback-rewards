package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.MonthlyCashbackReportUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRepository;
import com.serenitydojo.cashback_rewards.application.port.out.MemberRepository;
import com.serenitydojo.cashback_rewards.domain.exception.MemberNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import com.serenitydojo.cashback_rewards.domain.model.MonthlyCashbackReport;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
public class MonthlyCashbackReportService implements MonthlyCashbackReportUseCase {

    private final MemberRepository members;
    private final CashbackRepository cashbacks;

    public MonthlyCashbackReportService(MemberRepository members, CashbackRepository cashbacks) {
        this.members = members;
        this.cashbacks = cashbacks;
    }

    @Override
    public MonthlyCashbackReport reportFor(String memberId, YearMonth month) {
        Member member = members.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return MonthlyCashbackReport.forMonth(month, member.timeZone(), cashbacks.findByCustomerId(memberId));
    }
}
