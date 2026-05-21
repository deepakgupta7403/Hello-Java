package Phase8_PracticalAPIs.DateAndTime;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Instant — a Moment on the Global Timeline
 * -----------------------------------------
 * An Instant is a single, unambiguous point in time, measured in
 * NANOSECONDS since 1970-01-01T00:00Z (the "epoch"). It has NO zone —
 * it's "the same instant" everywhere on Earth.
 * <p>
 *
 * When to use Instant
 * -------------------
 *   - LOGGING and EVENT TIMESTAMPS — what time something happened,
 *     globally, regardless of who's reading.
 *   - DATABASE timestamp columns (TIMESTAMPTZ in Postgres, etc.).
 *   - DURATIONS and DEADLINES — Instant + Duration is the canonical
 *     "deadline" pattern.
 * <p>
 *
 * When NOT to use it
 * ------------------
 *   - For HUMAN-FACING dates/times — display in a zone first
 *     (see ZonedDateTime).
 * <p>
 *
 * Construction
 * ------------
 *      Instant.now()                       - via system clock
 *      Instant.now(Clock)                  - via injected clock (testable!)
 *      Instant.ofEpochSecond(s [, nanos])
 *      Instant.ofEpochMilli(ms)
 *      Instant.parse("2026-05-20T09:00:00Z")  - ISO-8601, always Z
 * <p>
 *
 * Conversions
 * -----------
 *      instant.atZone(zone)                -> ZonedDateTime
 *      instant.atOffset(offset)            -> OffsetDateTime
 *      ZonedDateTime.toInstant()           -> Instant
 *      Date.from(instant) / instant.toEpochMilli()
 * <p>
 *
 * Comparing two instants
 * ----------------------
 *      Duration.between(a, b)             - clock-length difference
 *      ChronoUnit.SECONDS.between(a, b)
 *      a.isBefore(b) / isAfter / equals
 */

public class InstantDemo {

    public static void main(String[] args) {

        section("1) Construction");
        Instant now    = Instant.now();
        Instant epoch  = Instant.EPOCH;                  // 1970-01-01T00:00:00Z
        Instant parsed = Instant.parse("2026-05-20T09:00:00Z");
        System.out.println("now    = " + now);
        System.out.println("epoch  = " + epoch);
        System.out.println("parsed = " + parsed);

        section("2) From epoch numbers");
        Instant fromMillis  = Instant.ofEpochMilli(System.currentTimeMillis());
        Instant fromSecs    = Instant.ofEpochSecond(1_700_000_000L);
        System.out.println("from millis  = " + fromMillis);
        System.out.println("from seconds = " + fromSecs);

        section("3) Arithmetic — Instant + Duration");
        Instant deadline = now.plus(Duration.ofMinutes(30));
        System.out.println("now     = " + now);
        System.out.println("+30m    = " + deadline);
        System.out.println("between = " + Duration.between(now, deadline));

        section("4) Convert to a zoned view for display");
        ZoneId india = ZoneId.of("Asia/Kolkata");
        ZoneId nyc   = ZoneId.of("America/New_York");
        ZonedDateTime inIndia = now.atZone(india);
        ZonedDateTime inNyc   = now.atZone(nyc);
        System.out.println("in Kolkata = " + inIndia);
        System.out.println("in NYC     = " + inNyc);
        // Both ZonedDateTime values point to the SAME Instant — only the
        // wall-clock representation differs.

        section("5) Test-friendly: inject a fixed Clock");
        Clock fixed = Clock.fixed(parsed, ZoneOffset.UTC);
        Instant snap = Instant.now(fixed);
        System.out.println("frozen Instant.now() = " + snap);

        section("6) Common bug — using LocalDateTime where Instant is needed");
        // LocalDateTime has no zone. If you "log" a LocalDateTime and
        // restore it later in a different zone, you've silently shifted.
        // Pattern: log Instants, display LocalDateTimes.

        section("7) Comparing");
        Instant a = Instant.now();
        Instant b = a.plusSeconds(5);
        System.out.println("a.isBefore(b)         = " + a.isBefore(b));
        System.out.println("seconds between       = " + ChronoUnit.SECONDS.between(a, b));

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
