package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.Member;

public interface RegisterMemberUseCase {
    void register(Member member);
}
