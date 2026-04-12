# Earning Cashback — Example Map

## Story
As a customer
I want to earn cashback on my qualifying purchases
so that I am rewarded for my loyalty.

---

## Rule: Cashback rate must depend on the member's tier at the time of the purchase

| Member tier | Purchase amount | Cashback earned |
|-------------|-----------------|-----------------|
| Standard    | $100.00         | $1.00           |
| Premium     | $100.00         | $2.00           |
| Standard    | $0.49           | $0.00 (rounds down from $0.0049) |
| Premium     | $0.25           | $0.01 (rounds half-up from $0.005) |

- **Counter-example:** The one where a Standard member upgrades to Premium mid-month — purchases made *before* the upgrade still earn 1%, purchases *after* earn 2%. The tier is locked at the moment the purchase posts, not recalculated retrospectively.

- **Questions:**
  - If a member downgrades from Premium to Standard, does cashback already earned that month stay at 2%?
  - Is tier determined at purchase time or at posting time (relevant for delayed authorisations)?
  - Are there any other tiers planned (e.g., trial, lapsed, corporate)?

---

## Rule: Cashback must only be earned on qualifying spend

- **Example:** The one where a customer buys $80 of groceries — qualifying — and earns cashback at their tier rate.

- **Counter-example:** The one where a customer "buys" a $100 gift card — gift cards are typically excluded from loyalty earning to prevent stacking, so no cashback is earned.

- **Counter-example:** The one where a customer pays a $5 account fee — fees, interest and cash-equivalents are not spend and earn no cashback.

- **Questions:**
  - What is the authoritative list of non-qualifying categories? (gift cards, cash advances, fees, taxes, charity, betting?)
  - Are taxes and shipping included in the qualifying amount, or stripped out before the rate is applied?
  - Do partially-qualifying baskets need line-level handling, or is qualification all-or-nothing per transaction?

---

## Rule: A member's cashback must be capped at $50 per calendar month (in their local timezone)

- **Example:** The one where a Premium member has earned $49.50 this month and makes a $100 qualifying purchase. The purchase would normally earn $2.00, but only $0.50 is awarded because the cap is reached. The remaining $1.50 is forfeited, not carried over.

- **Counter-example:** The one where a Premium member already at the $50 cap makes another qualifying purchase on the last day of the month — zero cashback is earned on that purchase.

- **Counter-example:** The one where a member in UTC+13 makes a purchase at 11pm local time on the 31st — it counts in the *member's* local month, even if it's already the 1st in UTC.

- **Counter-example:** The one where a member at the cap makes a purchase just after midnight on the 1st of the next month — the cap has reset and the purchase earns full cashback.

- **Questions:**
  - Is the cap the same for Standard and Premium, or does Premium get a higher cap to match the higher rate?
  - When the cap is partially consumed by a single purchase, do we tell the customer? (transparency vs. silent forfeiture)
  - Does unused cap carry over? (Assumed no — please confirm.)
  - What is the "customer timezone" — billing address, profile setting, or device?

---

## Rule: A refund must reverse the cashback originally earned on the refunded portion

- **Example:** The one where a Premium member buys $100, earns $2.00, then receives a full refund — the $2.00 is clawed back from their cashback balance.

- **Example:** The one where a Premium member buys $100, earns $2.00, then receives a $40 partial refund — $0.80 is clawed back, leaving $1.20 of cashback for that purchase.

- **Counter-example:** The one where the original purchase earned no cashback because the member was at the monthly cap — the refund reverses $0.00, since nothing was awarded in the first place.

- **Counter-example:** The one where the refund happens in a *later* month than the original purchase. The clawback is applied to the current balance, even though it relates to a previous month's earning. This can push the current month's balance negative.

- **Questions:**
  - Can a clawback push a member's cashback balance below zero, or do we floor at zero and absorb the loss?
  - If the original purchase consumed cap headroom, does the reversal *restore* that headroom for new earning in the same month?
  - How are refunds for non-qualifying purchases handled? (No-op, presumably.)
  - What about chargebacks vs. merchant-initiated refunds — same treatment?

---

## Rule: Cashback amounts must be calculated in USD and rounded half-up to the nearest cent

- **Example:** The one where a Standard member spends $12.34 — raw cashback is $0.1234, rounded to $0.12.

- **Example:** The one where a Standard member spends $12.50 — raw cashback is $0.125, rounds half-up to $0.13.

- **Counter-example:** The one where a member pays in a foreign currency. Out of scope for now — but we need to confirm whether multi-currency settlement is on the roadmap, since rounding then has to happen *after* FX conversion.

- **Questions:**
  - Do we calculate cashback per transaction and then sum, or sum qualifying spend and calculate once per month? (Different rounding outcomes.)
  - Confirm: half-up, not banker's rounding?
