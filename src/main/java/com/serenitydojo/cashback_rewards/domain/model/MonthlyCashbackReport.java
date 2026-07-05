package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

/**
 * A member's cashback for a single calendar month, resolved in the member's
 * local timezone: an event belongs to the month its posting date falls in when
 * viewed from the member's own timezone, not UTC. The total is the net sum of
 * every entry, and is $0.00 for a month with no qualifying activity.
 */
public record MonthlyCashbackReport(List<Entry> entries, BigDecimal total) {

    public record Entry(String merchantName, BigDecimal cashbackAmount) {
    }

    public static MonthlyCashbackReport forMonth(YearMonth month, ZoneId timeZone, List<CashbackRecord> records) {
        List<Entry> entries = records.stream()
                .filter(record -> YearMonth.from(record.postedAt().atZone(timeZone)).equals(month))
                .map(record -> new Entry(record.merchantName(), record.cashbackAmount()))
                .toList();
        BigDecimal total = entries.stream()
                .map(Entry::cashbackAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.DOWN);
        return new MonthlyCashbackReport(entries, total);
    }
}
