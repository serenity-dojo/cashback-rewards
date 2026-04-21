# Basic Cashback Calculation — Example Map

## Story
As a customer
I want to earn cashback on my purchases
so that I'm rewarded for shopping with partner merchants.

---

## Rule: Must calculate cashback as a percentage of the purchase amount using the merchant's configured rate

| Purchase Amount | Merchant Rate | Cashback |
|-----------------|---------------|----------|
| $100.00         | 2%            | $2.00    |
| $50.00          | 5%            | $2.50    |
| $200.00         | 1.5%          | $3.00    |
| $0.00           | 2%            | $0.00    |

- **Counter-example:** The one where two different merchants have the *same* rate — cashback on equal purchase amounts is identical, confirming the rate (not the merchant identity) drives the calculation.

---

## Rule: Must round cashback down to 2 decimal places

- **Example:** The one where a $33.33 purchase at a 5% merchant yields a raw cashback of $1.6665 — the customer is credited $1.66 (not $1.67).
- **Counter-example:** The one where the calculation falls on an exact cent ($100.00 at 2% = $2.00) — no rounding takes place.

---

## Rule: Must use the rate configured for the specific merchant where the purchase occurred

- **Example:** The one where the same customer spends $100.00 at Merchant A (2%) and $100.00 at Merchant B (5%) in the same day — earning $2.00 and $5.00 respectively, each posted against the correct merchant's rate.

---

## Rule: Must reject purchases at merchants that are not registered partners

- **Example:** The one where a purchase is submitted for a merchant with no cashback rate configured — the request is rejected with a domain error and no balance change occurs.
- **Counter-example:** The one where a partner merchant exists with a rate of 0% — this is a *valid* configuration and earns $0.00 cashback (accepted, not rejected).

---

## Rule: Must only credit cashback to customers enrolled in the rewards programme

- **Example:** The one where an enrolled customer makes a $100.00 purchase at a 2% merchant — $2.00 is credited to their rewards balance.
- **Counter-example:** The one where a purchase is submitted for a customer who is not enrolled — the request is rejected with a domain error and no balance is created.

---

## Rule: Must credit the calculated cashback to the customer's rewards balance

- **Example:** The one where a customer with a $10.00 balance earns $2.00 cashback — their new balance is $12.00.
- **Counter-example:** The one where a newly-enrolled customer with a $0.00 balance earns their first $2.00 cashback — the balance becomes $2.00.

---

## Rule: Must treat a negative purchase amount as a refund, deducting the corresponding cashback from the balance

- **Example:** The one where a customer originally earned $2.00 on a $100.00 purchase at a 2% merchant, then a -$100.00 refund is processed at the same merchant — $2.00 is deducted, returning the balance to its pre-purchase state.
- **Counter-example:** The one where the refund deduction exceeds the customer's current balance — the balance is allowed to go negative (e.g. a $0.50 balance refunded by $2.00 leaves a -$1.50 balance).
