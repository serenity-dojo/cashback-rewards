# Feature: Minimum Purchase Threshold for Cashback

## Story

As a cashback program operator
I want purchases below a minimum amount to earn no cashback
So that we don't spend more processing a reward than the reward is worth on tiny purchases

## Rule 1: Must not award cashback on purchases below the minimum threshold ($1.00)

The minimum purchase threshold is $1.00, applied as a system-wide constant across all product categories.

| Purchase amount | Cashback rate | Cashback awarded |
|-----------------|---------------|------------------|
| $0.50           | 2%            | $0.00            |
| $0.99           | 1%            | $0.00            |
| $1.00           | 2%            | $0.02            |
| $25.00          | 2%            | $0.50            |

The boundary at $1.00 is inclusive — purchases at exactly the threshold earn cashback normally.

## Rule 2: Should apply the threshold to the purchase amount, not the resulting cashback amount

- Example: The one where a $0.75 purchase at 2% earns $0.00 — the purchase amount ($0.75) is below the $1.00 threshold, so no cashback regardless of rate
- Counter-example: The one where a $5.00 purchase at 0.5% earns $0.02 — the cashback is tiny but the purchase exceeds the threshold, so it is still awarded

## Rule 3: Must not create a cashback record for below-threshold purchases

Below-threshold purchases are excluded entirely — no cashback record is saved. This is consistent with how non-partner purchases are handled today.

- Example: The one where a $0.50 purchase at a partner merchant results in no cashback record being stored
- Counter-example: The one where a $1.00 purchase at a partner merchant creates a cashback record with the calculated amount

## Rule 4: Should treat the threshold check as independent of other eligibility rules

The minimum threshold is one eligibility check among several (partner merchant, posted transaction, active card, purchase type). A transaction must pass all checks to earn cashback.

- Example: The one where a $0.80 purchase at a partner merchant earns no cashback — passes partner check but fails threshold check
- Example: The one where a $0.50 purchase at a non-partner merchant earns no cashback — fails partner check before threshold is even relevant
