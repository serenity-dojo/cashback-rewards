package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.MemberRepository;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Optional;

@Repository
class JpaMemberRepository implements MemberRepository {

    private final MemberJpaRepository members;

    JpaMemberRepository(MemberJpaRepository members) {
        this.members = members;
    }

    @Override
    public void save(Member member) {
        members.save(new MemberEntity(member.memberId(), member.timeZone().getId()));
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return members.findById(memberId).map(JpaMemberRepository::toDomain);
    }

    private static Member toDomain(MemberEntity entity) {
        return new Member(entity.getMemberId(), ZoneId.of(entity.getTimeZone()));
    }
}
