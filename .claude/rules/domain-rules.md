---
paths:
- "src/main/java/**/domain/**"
---

You are editing domain layer code.

NEVER import org.springframework
NEVER import jakarta.persistence
Business logic only — pure Java.
Use records for value objects.
BigDecimal for ALL monetary values.
Test with plain JUnit + AssertJ.