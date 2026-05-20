# `Instant` — a moment on the global timeline

A single, unambiguous point in time, measured in **nanoseconds since
1970-01-01T00:00Z** (the "epoch"). It has **no zone** — it's the same instant
everywhere on Earth.

## When `Instant` is right
- **Logging** and event timestamps.
- **Database** timestamp columns (e.g., Postgres `timestamptz`).
- **Deadlines** — `Instant` + `Duration`.
- **Versioning** records by time.

## When it's wrong
- **Human display** — convert to a `ZonedDateTime` first.
- **Calendars / business dates** — `LocalDate` is the right type.

## Construction
```java
Instant.now();                                    // system clock
Instant.now(clock);                               // injected (testable)
Instant.EPOCH;                                    // 1970-01-01T00:00:00Z
Instant.ofEpochSecond(seconds[, nanos]);
Instant.ofEpochMilli(ms);
Instant.parse("2026-05-20T09:00:00Z");            // ISO-8601, always 'Z'
```

## Arithmetic
| Operation | Method |
|---|---|
| Add a clock-length | `instant.plus(Duration.ofMinutes(30))` |
| Add seconds / nanos | `plusSeconds`, `plusNanos`, `plusMillis` |
| Truncate to a unit | `truncatedTo(ChronoUnit.SECONDS)` |
| Compare | `isBefore`, `isAfter`, `equals` |
| Difference | `Duration.between(a, b)` |

## Conversions
| To | Code |
|---|---|
| `ZonedDateTime` | `instant.atZone(zoneId)` |
| `OffsetDateTime` | `instant.atOffset(zoneOffset)` |
| Back to `Instant` | `zdt.toInstant()` |
| Legacy `Date` | `Date.from(instant)` / `date.toInstant()` |
| Epoch number | `toEpochMilli()` / `getEpochSecond()` |

## Test-friendly: `Clock`
```java
Clock fixed = Clock.fixed(Instant.parse("2026-05-20T09:00:00Z"), ZoneOffset.UTC);
Instant snap = Instant.now(fixed);                 // deterministic
```
Pass a `Clock` into your services instead of calling `Instant.now()` everywhere.
Tests inject a fixed or "tick" clock.

## Common bug
Logging a `LocalDateTime` and restoring it later in a different zone **silently
shifts** the moment. Log `Instant`s; display `LocalDateTime`s.

## Run
```bash
cd src
java Basics.DateAndTime.InstantDemo
```

## See also
- `ZonedDateTime.java` — when humans need to see it.
- `Duration.java` — what you add to an `Instant`.
- `LegacyDateConversions.java` — bridging old code.
