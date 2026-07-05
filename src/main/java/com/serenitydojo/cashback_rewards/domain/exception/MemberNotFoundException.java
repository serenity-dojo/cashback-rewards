package com.serenitydojo.cashback_rewards.domain.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String memberId) {
        super("No member registered with id " + memberId);
    }
}
