package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Member;

import java.util.Optional;

public interface MemberRepository {
    void save(Member member);

    Optional<Member> findById(String memberId);
}
