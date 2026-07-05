package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
class MemberEntity {

    @Id
    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "time_zone", nullable = false)
    private String timeZone;

    protected MemberEntity() {
        // required by JPA
    }

    MemberEntity(String memberId, String timeZone) {
        this.memberId = memberId;
        this.timeZone = timeZone;
    }

    String getMemberId() {
        return memberId;
    }

    String getTimeZone() {
        return timeZone;
    }
}
