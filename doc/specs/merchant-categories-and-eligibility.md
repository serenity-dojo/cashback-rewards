# Merchant Categories & Eligibility

**As the card rewards product manager, I want cashback rates to vary by merchant category and only apply to eligible transactions, so that we can offer competitive rates on high-value categories while controlling exposure.**

## Rules and Examples

### Rule: Should award cashback at the rate configured for the transaction's product category

| Product Category (MCC) | Purchase Amount | Cashback |
|---|---|---|
| Groceries (5411) | $100.00 | $2.00 |
| Fuel (5541) | $100.00 | $1.00 |
| Other / unmapped MCC (e.g. 5912 Pharmacy) | $100.00 | $0.50 |

Each transaction carries an MCC (Merchant Category Code) identifying the **product category** of that purchase — it is a property of the transaction, not of the merchant. The cashback rate is derived from that product category alone — there is **no per-merchant rate override**. This supersedes the per-merchant configured rate from the basic-cashback-calculation spec. (Merchants themselves only carry a partner flag — see the partner-merchant rule in the basic-cashback-calculation spec.)

Product categories and their rates — including the default — are **admin-configurable**. Groceries (2%) and Fuel (1%) are the categories defined at launch; any transaction whose MCC isn't assigned to a configured category falls back to the **default 0.5%** "Other" rate (the unmapped-MCC row).

---

### Rule: Should only award cashback for posted transactions

A transaction is "posted" once it has settled with the card network. **"Posted" and "settled" refer to the same lifecycle state** (the basic-cashback-calculation spec uses "settled").

- **Example:** The one where a $50 grocery purchase has posted and earns $1.00.
- **Counter-example:** The one where a $50 grocery purchase is still pending — no cashback is awarded; the system waits until the transaction posts.

---

### Rule: Should only award cashback for transactions made on a card that was active at the time of the purchase

Card status is evaluated **at the time the purchase was authorised**, not at posting. Freezes or cancellations applied after the purchase do not affect cashback for purchases already authorised.

- **Example:** The one where a $50 fuel purchase is made on an active card and earns $0.50.
- **Counter-example:** The one where a $50 fuel purchase is made on a card that was frozen (or cancelled) at the time of purchase — no cashback is awarded.
- **Counter-example:** The one where a $50 fuel purchase is authorised on an active card on Monday, the card is frozen on Tuesday, and the transaction posts on Wednesday — the $0.50 cashback is still awarded because the card was active at the time of purchase.

---

### Rule: Should only award cashback for transactions classified as purchases

Anything not classified as a purchase — refunds, fees of any kind, adjustments — earns no cashback under this rule. Refund handling for *previously-earned* cashback is governed by the existing reversal rule in the basic-cashback-calculation spec; this rule only says the refund transaction itself earns nothing.

- **Example:** The one where a $40 grocery purchase earns $0.80 cashback.
- **Counter-example:** The one where a $40 grocery refund is posted — no cashback is awarded for the refund itself.
- **Counter-example:** The one where a $5 foreign transaction fee is posted — no cashback is awarded.