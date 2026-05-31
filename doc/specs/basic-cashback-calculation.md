# Basic Cashback Calculation

**As a customer, I want to earn cashback on my purchases so that I'm rewarded for shopping with partner merchants.**

## Rules and Examples

### Rule: Must calculate cashback as the merchant's configured rate multiplied by the purchase amount

| Merchant Rate | Purchase Amount | Cashback |
|---|---|---|
| 5%   | $100.00 | $5.00 |
| 2.5% | $40.00  | $1.00 |
| 10%  | $25.00  | $2.50 |
| 0%   | $100.00 | $0.00 |

The 0% row is the boundary case: a partner merchant configured with no cashback yields nothing, but the calculation still runs.

---

### Rule: Must only award cashback for purchases at partner merchants

- **Example:** The one where a customer pays $80 at GreenGrocer (partner, 3%) and earns $2.40.
- **Counter-example:** The one where a customer pays $80 at a non-partner café — no cashback record is created and the purchase is silently ignored by the rewards system.

---

### Rule: Must use the cashback rate that was effective for the merchant at the timestamp of the purchase

- **Example:** The one where Acme's rate changes from 2% to 5% effective `2026-05-01 14:00:00`. A purchase at 13:59:59 earns 2%; a purchase at 14:00:01 earns 5%.
- **Counter-example:** The one where an admin updates Acme's rate today — purchases made (and cashback already calculated) yesterday are not recalculated.

---

### Rule: Must credit cashback to the customer's available rewards balance only after the transaction has settled

- **Example:** The one where a customer with a $12.00 available balance makes a $50 purchase at 4% on Monday; the $2.00 is credited to the available balance when the transaction settles on Wednesday, bringing it to $14.00.
- **Counter-example:** The one where a purchase is made but has not yet settled — the available balance is unchanged until settlement.

---

### Rule: Must show cashback from unsettled transactions as "pending" on the customer's rewards balance

- **Example:** The one where a customer makes a $50 purchase at 4% on Monday; before settlement, their rewards view shows the existing available balance plus a separate "$2.00 pending" entry tied to the Monday purchase.
- **Counter-example:** The one where the transaction settles on Wednesday — the $2.00 moves from the pending bucket into the available balance and is no longer shown as pending.

---

### Rule: Must round cashback to whole cents using banker's rounding (half-even)

| Computed Cashback | Credited Cashback |
|---|---|
| $1.6649 | $1.66 |
| $1.6650 | $1.66 |
| $1.6651 | $1.67 |
| $1.6750 | $1.68 |

The two `.5`-exact rows show the half-even boundary: ties round to the nearest even cent.

---

### Rule: Must not award cashback for non-positive purchase amounts

- **Example:** The one where a $0.00 purchase (e.g. a free promotional item rung up at a partner merchant) produces no cashback record.
- **Counter-example:** Refunds (negative amounts) are not handled by this rule — see the reversal rule below.

---

### Rule: Must reverse cashback in full when any refund is processed against a settled transaction

- **Example:** The one where a customer's $100 purchase at 5% earned $5.00, and a full refund of the purchase deducts $5.00 from their balance.
- **Counter-example:** The one where the same $100 purchase is partially refunded for $40 — the entire $5.00 is still deducted, because any refund reverses the full cashback for that transaction.

---

### Rule: Merchant cashback rates must be configured to at most two decimal places

- **Example:** The one where a merchant is configured at 1.25% — valid and used as-is.
- **Counter-example:** The one where an admin attempts to configure a merchant at 1.255% — the configuration is rejected as invalid.
