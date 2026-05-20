# `Duration` — clock-length difference

Measures **exact, machine-level** time — seconds and nanos. "2 hours 30 min
15 sec." Used with `Instant`, `LocalTime`, and any precision below a day.

> **Duration vs Period:** `Duration` is *clock*. `Period` is *calendar*. A day
> isn't always 24 hours of clock time (DST!), so use `Period` for "X calendar
> days" and `Duration` for "X clock hours."

## Construction
| | |
|---|---|
| `Duration.ofNanos(n)` | nanoseconds |
| `Duration.ofMillis(n)` | milliseconds |
| `Duration.ofSeconds(n)` | seconds |
| `Duration.ofMinutes(n)` | minutes |
| `Duration.ofHours(n)` | hours |
| `Duration.ofDays(n)` | days (24h flat — not DST-aware) |
| `Duration.of(amount, ChronoUnit)` | flexible |
| `Duration.between(a, b)` | signed difference |
| `Duration.parse("PT2H30M15S")` | ISO-8601 |

## Arithmetic
`plus`, `minus`, `multipliedBy`, `dividedBy`, `negated`, `abs`.

## Accessors
| Method | Returns |
|---|---|
| `toNanos` / `toMillis` / `toSeconds` / `toMinutes` / `toHours` / `toDays` | Total in that unit |
| `toHoursPart` / `toMinutesPart` / `toSecondsPart` / `toNanosPart` (Java 9+) | Component pieces |
| `isNegative()` / `isZero()` | Sign |

## ISO-8601 format
- `PT` prefix for "time-of-period"
- `H` hours, `M` minutes, `S` seconds, fractional allowed
- Examples: `PT5S`, `PT2H30M`, `PT0.5S`, `-PT15M`

## Common use cases
| Use | Code |
|---|---|
| Cache TTL | `expiresAt = now.plus(Duration.ofMinutes(10))` |
| HTTP timeout | `.connectTimeout(Duration.ofSeconds(5))` |
| Retry backoff | `backoff = backoff.multipliedBy(2)` |
| Thread sleep (Java 19+) | `Thread.sleep(Duration.ofMillis(200))` |
| Test timeout | `assertTimeout(Duration.ofMillis(50), () -> ...)` |

## Run
```bash
cd src
java Basics.DateAndTime.DurationDemo
```

## See also
- `PeriodDemo.java` — the calendar-length cousin.
- `InstantDemo.java` — the value `Duration` typically goes between.
