# Legacy ↔ Modern Date Conversions

Most code should be `java.time`. But you'll meet legacy APIs:
- `java.util.Date` — a mutable long wrapper.
- `java.util.Calendar` — usually `GregorianCalendar`.
- `java.sql.Date` / `Time` / `Timestamp` — JDBC subclasses.
- `java.util.TimeZone` — legacy zone class.

## Conversion table
| From → To | Code |
|---|---|
| `Date` → `Instant` | `date.toInstant()` |
| `Instant` → `Date` | `Date.from(instant)` |
| `Date` → `LocalDate` | `date.toInstant().atZone(zone).toLocalDate()` |
| `LocalDate` → `Date` | `Date.from(ld.atStartOfDay(zone).toInstant())` |
| `Calendar` → `ZonedDateTime` | `cal.toInstant().atZone(cal.getTimeZone().toZoneId())` |
| `ZonedDateTime` → `Calendar` | `GregorianCalendar.from(zdt)` |
| `java.sql.Timestamp` ↔ `Instant` | `.toInstant()` / `Timestamp.from(instant)` |
| `java.sql.Date` ↔ `LocalDate` | `.toLocalDate()` / `java.sql.Date.valueOf(ld)` |
| `java.sql.Time` ↔ `LocalTime` | `.toLocalTime()` / `java.sql.Time.valueOf(lt)` |
| `TimeZone` ↔ `ZoneId` | `tz.toZoneId()` / `TimeZone.getTimeZone(zoneId)` |

## Best practice — convert at the boundary
```java
Instant in = receiveLegacy(date);     // outside boundary
// ... internal code uses only java.time ...
Date out  = exposeLegacy(instant);    // outside boundary
```
Never let `Date` / `Calendar` spread into domain code.

## Subtle traps
- **Zone-dependence**: `Date → LocalDate` needs a `ZoneId`. The same `Date` is "2026-05-20" in Mumbai and "2026-05-19" in Los Angeles. Pick a zone explicitly.
- **`java.sql.Date`** is `LocalDate`-like but inherits from `java.util.Date` — `getHours()` etc. throw. Don't read time-of-day off it.
- **Hardcoded UTC**: `cal.setTimeZone(TimeZone.getTimeZone("UTC"))` before converting if you want to ignore zone effects.

## Modern JDBC (Java 8+)
Drivers support `java.time` directly:
```java
preparedStatement.setObject(1, localDate);
preparedStatement.setObject(2, instant);
ResultSet rs = ...;
LocalDate ld = rs.getObject("birthday", LocalDate.class);
```
Prefer that over `setDate(...)` / `getDate(...)`.

## Run
```bash
cd src
java Basics.DateAndTime.LegacyDateConversions
```

## See also
- `InstantDemo.java`, `ZonedDateTimeDemo.java` — the modern types.
- `DateTimeFormatterDemo.java` — for parsing strings from legacy logs.
