# Total Cashback Paid per Product — Example Map

## Story
As a rewards program manager,
I want to see the total cashback we have paid out for a given product,
so that I can understand the cost of the rewards program by product.

> In this system cashback is recorded against a **product category**
> (e.g. Groceries, Fuel, Other). "Product" therefore means product category,
> identified by its category **name**. The total is **system-wide** — summed
> across every customer.

---

## Rule: Must total the cashback paid for a product as the sum of every cashback record for that product, across all customers

- **Example:** The one where Groceries earned cashback for two different customers — $2.40 and $1.60 — the total paid for Groceries is **$4.00**.
- **Counter-example:** The one where Fuel also has $5.00 of cashback — Fuel is excluded from the Groceries total, which stays $4.00.

---

## Rule: Must return a total of $0.00 for a product with no cashback records

- **Example:** The one where no purchase has ever earned cashback in Travel — the total is **$0.00**, returned as a normal result, not a 404 or error.
- **Counter-example:** The one where the name was never registered as a category — it is treated the same as a registered product with no payments: total $0.00. A query for a product is never an error.

---

## Rule: Must report the product, the total cashback paid, and the number of cashback payments counted

- **Example:** The one where Groceries has two payments totalling $4.00 — the result reports product "Groceries", total $4.00, count 2.
- **Counter-example:** The one where Travel has no payments — the result reports product "Travel", total $0.00, count 0.

---

## Rule: Must express the total in whole cents, consistent with how cashback is recorded (2 decimal places)

| Cashback payments for the product | Total  |
|-----------------------------------|--------|
| $2.40, $1.60                      | $4.00  |
| $0.50, $0.50, $0.50               | $1.50  |
| (none)                            | $0.00  |

---

## Resolved decisions (for implementation)

- **Endpoint:** `GET /api/products/{productCategory}/cashback-total`
- **Identified by:** product category **name** (path variable; URL-encode names containing spaces).
- **Scope:** all customers (system-wide).
- **Response (200):** `{ "product": "Groceries", "totalCashback": 4.00, "recordCount": 2 }`
- **Empty / unknown product:** `200 OK` with `totalCashback` `0.00` and `recordCount` `0`.
- Out of scope: refunds / clawbacks (not modelled in the system — every cashback record is a positive payment).
