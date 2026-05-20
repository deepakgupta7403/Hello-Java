package Phase8_PracticalAPIs.DateAndTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Duration — a Clock-Length Difference
 * ------------------------------------
 * Duration measures EXACT, MACHINE-LEVEL TIME — seconds and nanos.
 * "2 hours 30 minutes 15 seconds." Used with Instant, LocalTime, and
 * any precision below a day.
 *
 * Contrast with Period (P1Y2M3D) which is CALENDAR-LENGTH — variable
 * because months / years differ.
 *
 *
 * Construction
 * ------------
 *   Duration.ofNanos / ofMillis / ofSeconds / ofMinutes / ofHours / ofDays
 *   Duration.of(amount, ChronoUnit)
 *   Duration.between(a, b)              - signed difference
 *   Duration.parse("PT2H30M15S")        - ISO-8601 duration text
 *
 *
 * Arithmetic
 * ----------
 *   plus / minus / multipliedBy / dividedBy / negated / abs
 *
 *
 * Accessors
 * ---------
 *   toDays / toHours / toMinutes / toSeconds / toMillis / toNanos
 *   toDaysPart / toHoursPart / toMinutesPart / toSecondsPart / toNanosPart  (Java 9+)
 *   isNegative / isZero
 *
 *
 * Common use cases
 * ----------------
 *   - Timeouts: thread.join(Duration.ofSeconds(5))                  (Java 19+)
 *   - HTTP timeouts: HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
 *   - Cache TTLs.
 *   - Retry backoff schedules.
 *
 *
 * Pitfall: Duration.ofDays uses 24 hours, NOT a calendar day. Over a
 * DST boundary "1 day" can be 23 or 25 hours of clock time — Period
 * understands that; Duration doesn't.
 */

public class DurationDemo {

    public static void main(String[] args) {

        section("1) Construction");
        Duration d1 = Duration.ofMinutes(90);
        Duration d2 = Duration.of(45, ChronoUnit.SECONDS);
        Duration d3 = Duration.parse("PT2H30M15S");
        System.out.println("d1 = " + d1);
        System.out.println("d2 = " + d2);
        System.out.println("d3 = " + d3);

        section("2) between() — signed difference");
        Instant a = Instant.now();
        Instant b = a.plus(Duration.ofMinutes(5));
        System.out.println("Duration.between(a, b) = " + Duration.between(a, b));
        System.out.println("Duration.between(b, a) = " + Duration.between(b, a) + " (negative)");

        section("3) Arithmetic");
        Duration five = Duration.ofMinutes(5);
        System.out.println("plus 30s   = " + five.plusSeconds(30));
        System.out.println("times 4    = " + five.multipliedBy(4));
        System.out.println("divided 2  = " + five.dividedBy(2));
        System.out.println("negated    = " + five.negated());

        section("4) Accessors (Java 9+ '*Part' methods)");
        Duration mix = Duration.ofHours(2).plusMinutes(30).plusSeconds(15);
        System.out.println("toMinutes        = " + mix.toMinutes());      // total minutes
        System.out.println("toMinutesPart    = " + mix.toMinutesPart());  // 30
        System.out.println("toHoursPart      = " + mix.toHoursPart());    // 2
        System.out.println("toSecondsPart    = " + mix.toSecondsPart());  // 15

        section("5) Common pattern: cache TTL");
        Instant putAt = Instant.now();
        Duration ttl  = Duration.ofMinutes(10);
        Instant expiresAt = putAt.plus(ttl);
        System.out.println("expires at " + expiresAt);

        section("6) Common pattern: backoff schedule");
        Duration backoff = Duration.ofMillis(100);
        for (int attempt = 1; attempt <= 4; attempt++) {
            System.out.println("attempt " + attempt + ": sleep " + backoff);
            backoff = backoff.multipliedBy(2);
        }

        section("7) Pitfall: Duration.ofDays is 24h flat");
        // Over a DST boundary, '1 day' of wall-clock is sometimes 23 or 25 hours.
        // Use Period for calendar logic; Duration for clock logic.

        section("8) Java 19+: Thread.sleep(Duration)");
        try { Thread.sleep(Duration.ofMillis(20)); } catch (InterruptedException ignored) {}
        System.out.println("slept 20ms via Thread.sleep(Duration)");

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
