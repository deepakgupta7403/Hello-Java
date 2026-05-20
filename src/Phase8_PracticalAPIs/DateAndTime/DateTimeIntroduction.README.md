# Date and Time API — Introduction

Java 8 replaced the chaos of `java.util.Date` / `Calendar` / `SimpleDateFormat`
with a clean, **immutable, thread-safe** set of value types in **`java.time`**
(JSR-310). Use this for *everything* new.

## The headline types
| Type | Holds | Use for |
|---|---|---|
| `LocalDate` | Date, no time, no zone | A birthday, a deadline, a calendar entry |
| `LocalTime` | Time of day, no date | "Set the alarm to 06:30" |
| `LocalDateTime` | Date + time, no zone | Local event "next Tuesday at 14:30" |
| `ZonedDateTime` | Date + time + **zone** | "Meeting at 14:30 in Tokyo" |
| `OffsetDateTime` | Date + time + fixed offset | Database timestamps |
| `Instant` | A moment on the global timeline (epoch seconds + nanos) | Logging, system events |
| `Duration` | Clock length (`PT2H30M`) | "Sleep 2h30m" |
| `Period` | Calendar length (`P1Y2M3D`) | "Trial period of 30 days" |
| `Year`, `YearMonth`, `MonthDay`, `DayOfWeek`, `Month` | Narrow value types | Specific use cases |

## Key properties
- **Immutable** — every operation returns a new value.
- **Thread-safe** — share freely.
- **Fluent** — `.plusDays(2).minusHours(3).withYear(2030)`.
- **Value semantics** — `equals`, `hashCode`, `toString` all work.

## Modern conveniences
| Java | Feature |
|---|---|
| 9 | `LocalDate.datesUntil(end) → Stream<LocalDate>` |
| 12 | `LocalDate.formatted(formatter)` (via `String.formatted` & friends) |
| 17 | Pattern matching on date types in `switch` |

## ⚠️ Avoid these
- `java.util.Date` — mutable, no zone semantics, awful `toString`.
- `java.util.Calendar` — even worse.
- `SimpleDateFormat` — thread-unsafe.
- `Date.UTC` constants and other static cruft.

If a legacy API hands you a `Date`, **convert immediately**:
```java
Instant i = date.toInstant();
ZonedDateTime z = i.atZone(ZoneId.systemDefault());
```

## What to read next
| Focus | File |
|---|---|
| Dates and times without time zones | `LocalDateLocalTimeLocalDateTime.java` |
| Time stamps on the global timeline | `Instant.java` |
| Times with zones | `ZonedDateTime.java` |
| Clock-length differences | `Duration.java` |
| Calendar-length differences | `Period.java` |
| Parsing and printing | `DateTimeFormatter.java` |
| Bridging to legacy `Date` / `Calendar` | `LegacyDateConversions.java` |

## Run
```bash
cd src
java Basics.DateAndTime.DateTimeIntroduction
```
