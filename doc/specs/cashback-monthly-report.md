# Cashback Monthly Report — Example Map

## Story
As a customer
I want to see the details and total of my cashback earnings each month
so that I can understand and trust how my rewards are calculated.

---

## Rule: Must report cashback for a single calendar month in the member's local timezone

- **Example:** The one where a member requests their report for March 2026 — the report contains every cashback event with a posting date between 1 March 00:00 and 31 March 23:59 in the member's local timezone.

- **Counter-example:** The one where a member in UTC+13 has a transaction that posts at 11pm local on 31 March (already 1 April UTC) — it appears in the March report, not April, because we use the member's local month (consistent with how the monthly cap is applied).

- **Questions:**
  - Can a member request the *current* month-to-date, or only completed past months?
  - How far back can a member request? (Retention window — 12 months? 7 years for tax?)
  - If a member changes timezone mid-month, which timezone applies to the report?

---

## Rule: Must list each cashback event as a line entry, with refund clawbacks shown as negative amounts

- **Example:** The one where a Premium member buys $50 of groceries earning $1.00, then $30 of fuel earning $0.60, then receives a $50 refund clawing back $1.00 — the report has three entries: +$1.00, +$0.60, –$1.00.

- **Counter-example:** The one where a clawback relates to a purchase from a *previous* month — the clawback still appears in the month it actually happened, even though the original earning was reported in a different month.

- **Counter-example:** The one where a member buys a $100 gift card — gift cards are non-qualifying and earned no cashback, so they do not appear in the report at all (the report is about cashback events, not all purchases).

- **Questions:**
  - What identifying details does each entry need? (Date, merchant, original purchase amount, cashback amount?)
  - Are clawbacks shown linked back to the original purchase, or as standalone entries?
  - Are entries ordered chronologically, by amount, or grouped by merchant/category?

---

## Rule: Must show the monthly total as the net sum of all entries

| Earnings this month | Clawbacks this month | Reported total |
|---------------------|----------------------|----------------|
| $12.40              | $0.00                | $12.40         |
| $12.40              | $2.00                | $10.40         |
| $0.00               | $0.00                | $0.00          |
| $1.00               | $5.00                | –$4.00         |

- **Counter-example:** The one where a member has had no qualifying activity at all in the requested month — the report is returned with an empty list and a total of $0.00 (it is not an error or "no report" response).

- **Questions:**
  - Can the displayed total go negative, or do we floor at $0.00? (Linked to the open question on whether balances can go negative.)
  - Should the report also show year-to-date or lifetime totals for context, or strictly the requested month?

---

## Rule: Must indicate when the monthly cap was reached and how much potential cashback was forfeited

- **Example:** The one where a Premium member has earned $48.00 then makes a purchase that would have earned $5.00 but is capped at $2.00 — the report shows total earnings of $50.00, a forfeited amount of $3.00, and a clear indication that the cap was reached.

- **Counter-example:** The one where a member finishes the month at $30 of cashback — no cap indicator and no forfeited amount is shown, because the cap was never approached.

- **Questions:**
  - Do we want a numeric "would-have-earned" figure, or just a flag that the cap was hit?
  - If a clawback later in the same month frees up cap headroom, do we recalculate the forfeited amount retrospectively, or is forfeited a frozen historical fact?
  - Do we always show the cap value ($50) on the report, or only mention it when it has been hit?

---

## Rule: Must only return the requesting member's own cashback report

- **Example:** The one where a member requests "my March report" — they receive their own data and only their own data.

- **Counter-example:** The one where a customer-service agent requests a member's report on the member's behalf (e.g., to investigate a complaint) — out of scope for this story; agent access is a separate authorisation concern.

- **Questions:**
  - Are joint accounts in scope? If so, who can see whose entries?
  - Do we need an audit log of who viewed each report, or is "read-by-self only" sufficient?
