# `ZonedDateTime` and `OffsetDateTime`

| Type | Holds | Knows DST? |
|---|---|---|
| `ZonedDateTime` | `LocalDateTime` + `ZoneId` (e.g., `"Asia/Kolkata"`) | **Yes** |
| `OffsetDateTime` | `LocalDateTime` + `ZoneOffset` (e.g., `+05:30`) | **No** — fixed offset |

A `ZoneId` knows daylight-saving rules and historical changes. A `ZoneOffset`
is a static number of hours / minutes from UTC.

## When to use which
| Use | Pick |
|---|---|
| "Meeting at 14:30 in Tokyo on this date" | `ZonedDateTime` |
| Recurring schedule that respects DST | `ZonedDateTime` |
| Database `timestamptz` column | `OffsetDateTime` |
| ISO-8601 string with offset | `OffsetDateTime` |
| The global moment underneath | `Instant` |

## Conversions
| From → To | Code |
|---|---|
| `LocalDateTime` → `ZonedDateTime` | `ldt.atZone(zone)` |
| `Instant` → `ZonedDateTime` | `instant.atZone(zone)` |
| `ZonedDateTime` → `Instant` | `zdt.toInstant()` |
| Same instant, new zone | `zdt.withZoneSameInstant(other)` |
| Same wall clock, new zone | `zdt.withZoneSameLocal(other)` — rarely what you want |

## DST gotcha
On a "spring forward" night the wall clock jumps **02:00 → 03:00**; **02:30
doesn't exist** that night. The library handles it:
```java
ZonedDateTime z = ZonedDateTime.of(
        LocalDateTime.of(2026, 3, 8, 2, 30),
        ZoneId.of("America/New_York"));
// → 2026-03-08T03:30-04:00[America/New_York]
```

`LocalDateTime` doesn't even know there's a problem — never store a future
appointment as a `LocalDateTime` in NYC.

## Useful API
| Call | Purpose |
|---|---|
| `ZoneId.systemDefault()` | Current JVM zone |
| `ZoneId.of("Asia/Kolkata")` | A specific zone |
| `ZoneId.getAvailableZoneIds()` | Set of all known zones |
| `ZonedDateTime.now()` / `ZonedDateTime.now(zone)` | Construct |
| `z.toLocalDate()` / `toLocalTime()` / `toLocalDateTime()` | Drop the zone |
| `z.format(formatter)` | Print |

## Formatting for humans
```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a zzz");
System.out.println(z.format(fmt));
```

## Run
```bash
cd src
java Basics.DateAndTime.ZonedDateTimeDemo
```

## See also
- `InstantDemo.java` — the timeline underneath.
- `LocalDateLocalTimeLocalDateTime.java` — the zone-less local types.
- `DateTimeFormatter.java` — format and parse.
