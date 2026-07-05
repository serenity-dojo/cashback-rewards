package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.MonthlyCashbackReportUseCase;
import com.serenitydojo.cashback_rewards.application.port.in.RegisterMemberUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.MemberNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import com.serenitydojo.cashback_rewards.domain.model.MonthlyCashbackReport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final RegisterMemberUseCase registerMember;
    private final MonthlyCashbackReportUseCase monthlyReport;

    public MemberController(RegisterMemberUseCase registerMember, MonthlyCashbackReportUseCase monthlyReport) {
        this.registerMember = registerMember;
        this.monthlyReport = monthlyReport;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody MemberRequest request) {
        registerMember.register(new Member(request.memberId(), ZoneId.of(request.timeZone())));
    }

    @GetMapping("/{memberId}/cashback-report")
    public ReportResponse report(@PathVariable String memberId, @RequestParam String month) {
        MonthlyCashbackReport report = monthlyReport.reportFor(memberId, YearMonth.parse(month));
        List<EntryResponse> entries = report.entries().stream()
                .map(entry -> new EntryResponse(entry.merchantName(), entry.cashbackAmount()))
                .toList();
        return new ReportResponse(entries, report.total());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleMemberNotFound(MemberNotFoundException ignored) {
    }

    public record MemberRequest(
            @NotBlank String memberId,
            @NotBlank String timeZone) {
    }

    public record ReportResponse(List<EntryResponse> entries, BigDecimal total) {
    }

    public record EntryResponse(String merchantName, BigDecimal cashbackAmount) {
    }
}
