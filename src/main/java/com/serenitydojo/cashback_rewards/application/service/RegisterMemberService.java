package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMemberUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.MemberRepository;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import org.springframework.stereotype.Service;

@Service
public class RegisterMemberService implements RegisterMemberUseCase {

    private final MemberRepository members;

    public RegisterMemberService(MemberRepository members) {
        this.members = members;
    }

    @Override
    public void register(Member member) {
        members.save(member);
    }
}
