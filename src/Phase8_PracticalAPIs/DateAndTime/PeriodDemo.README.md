# `Period` — calendar-length difference

Represents an interval in **years, months, days** — the units humans actually
talk about for ages and trial periods.

| Example | Means |
|---|---|
| `P1Y2M3D` | 1 year, 2 months, 3 days |
| `P10D` | 10 days |
| `P-1M` | "minus 1 month" |

Unlike `Duration`, `Period` is **calendar-aware**: adding 1 month to Jan 31
**clamps** to the last valid day of the next month — `2026-02-28`.

## Construction
| | |
|---|---|
| `Period.ofDays(n)` / `ofWeeks(n)` / `ofMonths(n)` / `ofYears(n)` | One unit |
| `Period.of(y, m, d)` | All three |
| `Period.between(start, end)` | `LocalDate` → `LocalDate` |
| `Period.parse("P1Y2M3D")` | ISO-8601 |

## Arithmetic & accessors
| | |
|---|---|
| `plus` / `minus` / `multipliedBy` / `negated` | Arithmetic |
| `normalized()` | Roll 13 months into `1Y1M` |
| `getYears()` / `getMonths()` / `getDays()` | Field accessors |
| `isZero()` / `isNegative()` | Predicates |

## Use cases
| Use | Example |
|---|---|
| Age | `Period.between(birthday, today).getYears()` |
| Trial expiry | `today.plus(Period.ofDays(30))` |
| Anniversary | `today.plus(Period.ofYears(1))` |
| Subscription billing | `nextBill = startDate.plus(Period.ofMonths(1))` |

## The Jan 31 + 1 month puzzle
```java
LocalDate.of(2026, 1, 31).plus(Period.ofMonths(1));   // 2026-02-28
```
The library **clamps** to the last valid day. Stable, but **non-commutative**
in a subtle way:
```java
jan31.plusMonths(1).plusMonths(1) != jan31.plusMonths(2);
```
The first goes Jan 31 → Feb 28 → Mar 28; the second goes Jan 31 → Mar 31.
Always decide which behaviour you want when accumulating.

## Don't confuse calendar with clock
- "Calendar days" — `Period` / `ChronoUnit.DAYS.between(a, b)`.
- "Clock hours / seconds / nanos" — `Duration`.

## Run
```bash
cd src
java Basics.DateAndTime.PeriodDemo
```

## See also
- `DurationDemo.java` — the clock-length cousin.
- `LocalDateLocalTimeLocalDateTime.java` — what you add `Period` to.
