# `LocalDate` / `LocalTime` / `LocalDateTime`

Three "local" types — **no time zone, no offset**. Use them when the time zone
is implicit or doesn't matter.

| Type | Example | Use for |
|---|---|---|
| `LocalDate` | `2026-05-20` | Birthday, deadline, calendar entry |
| `LocalTime` | `14:30:00` | Alarm, opening hours |
| `LocalDateTime` | `2026-05-20T14:30` | Local event "Tuesday at 14:30" |

## Construction
```java
LocalDate.now();                          // today, system zone
LocalDate.of(2026, 5, 20);
LocalDate.of(2026, Month.MAY, 20);
LocalDate.parse("2026-05-20");

LocalTime.of(14, 30);
LocalTime.of(14, 30, 15, 123_000_000);

LocalDateTime.of(date, time);
LocalDateTime.of(2026, 5, 20, 14, 30);
```

## Arithmetic (immutable — every op returns a new value)
| Method | Effect |
|---|---|
| `plusDays`, `minusDays`, `plusWeeks`, `plusMonths`, `plusYears` | Add / subtract |
| `withYear`, `withMonth`, `withDayOfMonth`, `withDayOfWeek` | Replace one field |
| `with(TemporalAdjuster)` | Named adjustments — see below |
| `truncatedTo(ChronoUnit)` | "Round down" to a unit |

## `TemporalAdjusters` — natural-language adjustments
```java
date.with(TemporalAdjusters.firstDayOfMonth());
date.with(TemporalAdjusters.lastDayOfMonth());
date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
date.with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.TUESDAY));  // 2nd Tuesday
date.with(TemporalAdjusters.lastDayOfYear());
```
You can also pass a lambda `t -> ...` for custom adjustments (e.g., "last weekday of the month").

## Querying
| Method | Returns |
|---|---|
| `getYear()` / `getMonth()` / `getDayOfMonth()` | The field |
| `getDayOfWeek()` / `getDayOfYear()` | Calendar info |
| `lengthOfMonth()` / `lengthOfYear()` / `isLeapYear()` | Calendar facts |
| `isBefore(other)` / `isAfter(other)` / `equals(other)` | Comparison |
| `compareTo(other)` | `int` comparison for `Comparator` |

## Differences
| Goal | Code |
|---|---|
| Whole days between | `ChronoUnit.DAYS.between(a, b)` |
| Years/months/days between | `a.until(b)` → returns `Period` |
| Stream of dates from `a` to `b` (Java 9+) | `a.datesUntil(b)` |
| Stream with stride | `a.datesUntil(b, Period.ofMonths(1))` |

## Run
```bash
cd src
java Basics.DateAndTime.LocalDateLocalTimeLocalDateTime
```

## See also
- `Instant.java`, `ZonedDateTime.java` — when zones matter.
- `Period.java`, `Duration.java` — encoding differences.
- `DateTimeFormatter.java` — parsing and printing.
