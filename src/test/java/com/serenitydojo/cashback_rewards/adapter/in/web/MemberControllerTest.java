package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.MonthlyCashbackReportUseCase;
import com.serenitydojo.cashback_rewards.application.port.in.RegisterMemberUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@DisplayName("MemberController")
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RegisterMemberUseCase registerMember;

    @MockitoBean
    MonthlyCashbackReportUseCase monthlyReport;

    @Test
    @DisplayName("GET report for an unregistered member returns 404 Not Found")
    void returnsNotFoundForUnknownMember() throws Exception {
        when(monthlyReport.reportFor(eq("mem-unknown"), any(YearMonth.class)))
                .thenThrow(new MemberNotFoundException("mem-unknown"));

        mockMvc.perform(get("/api/members/{memberId}/cashback-report", "mem-unknown")
                        .param("month", "2026-03"))
                .andExpect(status().isNotFound());
    }
}
