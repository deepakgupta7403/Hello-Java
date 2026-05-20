# `DateTimeFormatter`

Parse strings into date / time values and format them back. **Thread-safe**
and **immutable** — share instances freely. Replaces the legacy (and broken)
`SimpleDateFormat`.

## Three ways to get a formatter
| | |
|---|---|
| **Constants** | `DateTimeFormatter.ISO_LOCAL_DATE` etc. for ISO-8601 |
| **Pattern** | `DateTimeFormatter.ofPattern("dd/MM/yyyy")` |
| **Builder** | `new DateTimeFormatterBuilder()...` for fine control |

## Useful constants
| Constant | Format |
|---|---|
| `ISO_LOCAL_DATE` | `2026-05-20` |
| `ISO_LOCAL_TIME` | `14:30:00` |
| `ISO_LOCAL_DATE_TIME` | `2026-05-20T14:30:00` |
| `ISO_OFFSET_DATE_TIME` | `2026-05-20T14:30:00+05:30` |
| `ISO_ZONED_DATE_TIME` | `...+05:30[Asia/Kolkata]` |
| `ISO_INSTANT` | `2026-05-20T09:00:00Z` |
| `BASIC_ISO_DATE` | `20260520` |

## Pattern letters
| Letter | Meaning |
|---|---|
| `y` / `u` | Year (`u` allows negative for BCE) |
| `M` | Month-of-year — `MM=07`, `MMM=Jul`, `MMMM=July` |
| `d` | Day-of-month |
| `E` | Day-of-week — `E=Mon`, `EEEE=Monday` |
| `H` / `h` | Hour 0–23 / 1–12 |
| `m` / `s` | Minute / second |
| `S` / `n` | Fraction-of-second / nano |
| `a` | AM/PM |
| `z` | Zone abbreviation (`PST`) |
| `V` | Zone id (`America/Los_Angeles`) |
| `x` / `X` | Offset (`+0530` / `+05:30`) |

## Locale matters
Month and day names depend on `Locale`:
```java
DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH)
```
Always **pin the locale** for output that crosses borders. The platform
default is a portability hazard.

## Localized helpers
```java
DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.US);
DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG);
```

## Parsing
```java
LocalDate.parse("2026-05-20");                                   // ISO by default
LocalDate.parse("20/05/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
ZonedDateTime.parse("2026-05-20T14:30+05:30[Asia/Kolkata]");
```
On failure → `DateTimeParseException`.

## Strict / lenient parsing via builder
```java
DateTimeFormatter strict = new DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
    .appendLiteral('-')
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .appendLiteral('-')
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .toFormatter();
```

## Tips
- For **machine interchange**, prefer ISO constants — unambiguous and locale-free.
- For **human display**, prefer `FormatStyle` with explicit `Locale`.
- For **logging**, log `Instant` (ISO-8601 `Z`) — no zone ambiguity.

## Run
```bash
cd src
java Basics.DateAndTime.DateTimeFormatterDemo
```

## See also
- All other files in this folder — they use `DateTimeFormatter` for display.
- `LegacyDateConversions.java` — bridging to `Date`.
