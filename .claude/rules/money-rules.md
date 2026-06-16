---
paths:
  - "src/main/java/**"
---

You are editing code that may handle monetary values.

BigDecimal for ALL monetary values — NEVER float, double, or int.
Construct with BigDecimal.valueOf(...) or new BigDecimal("...") — NEVER new BigDecimal(double).
Always specify an explicit RoundingMode.
Cashback amounts: RoundingMode.HALF_EVEN (banker's rounding), scale 2.
In tests, compare amounts with isEqualByComparingTo("1.60"), never isEqualTo.
