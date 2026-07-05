package com.serenitydojo.cashback_rewards.domain.model;

import java.time.ZoneId;

public record Member(String memberId, ZoneId timeZone) {
}
