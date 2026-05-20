package Phase8_PracticalAPIs.DateAndTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Date and Time API — Introduction (java.time, since Java 8)
 * ----------------------------------------------------------
 * Java 8 replaced the chaos of java.util.Date / Calendar / SimpleDateFormat
 * with a clean, immutable, thread-safe set of value types in java.time.
 *
 *
 * The headline types
 * ------------------
 *   LocalDate         - a date with NO time, NO zone     (2026-05-20)
 *   LocalTime         - a time with NO date, NO zone     (14:30:00.000)
 *   LocalDateTime     - both, but still NO zone           (2026-05-20T14:30)
 *   ZonedDateTime     - date+time WITH a time zone        (2026-05-20T14:30+05:30[Asia/Kolkata])
 *   OffsetDateTime    - date+time + fixed offset, NO zone (2026-05-20T14:30+05:30)
 *   Instant           - a moment on the global timeline   (epoch-seconds + nanos)
 *   Duration          - a length of time on the clock     (PT2H30M)
 *   Period            - a length of time on the calendar  (P1Y2M3D)
 *   Year, YearMonth, MonthDay, DayOfWeek, Month  - narrow value types
 *
 *
 * Key properties
 * --------------
 *   IMMUTABLE        - every operation returns a new value.
 *   THREAD-SAFE      - share freely.
 *   FLUENT           - .plusDays(2).minusHours(3).withYear(...)
 *   VALUE SEMANTICS  - equals/hashCode/toString all work.
 *
 *
 * Java 8 was a long time ago — what's new since?
 * ----------------------------------------------
 *   Java 9   - LocalDate.datesUntil(end) -> Stream<LocalDate>
 *   Java 12  - LocalDate.formatted(formatter)
 *   Java 17  - Pattern matching on dates in switch
 *   Java 21  - Sequenced collections (returns LocalDate ordering helpers)
 *
 *
 * This file is the orientation. Each of the headline types gets its own
 * dedicated file with detailed examples.
 */

public class DateTimeIntroduction {

    public static void main(String[] args) {

        section("1) LocalDate / LocalTime / LocalDateTime");
        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("today = " + today);
        System.out.println("now   = " + now);
        System.out.println("ldt   = " + ldt);

        section("2) Immutability — operations return new values");
        LocalDate t = today;
        LocalDate plus = t.plusDays(7);
        System.out.println("today           = " + t);
        System.out.println("today.plusDays7 = " + plus);
        System.out.println("today unchanged = " + t);

        section("3) Why a separate Period / Duration");
        // PERIOD is calendar-based (years/months/days) — DST aware.
        // DURATION is clock-based (seconds/nanos) — exact.
        java.time.Period p = java.time.Period.between(LocalDate.of(2020, 1, 1), today);
        java.time.Duration d = java.time.Duration.between(
                LocalTime.of(0, 0), now);
        System.out.println("period since 2020-01-01 = " + p);
        System.out.println("duration since midnight = " + d);

        section("4) Use ISO-8601 strings to parse and print");
        LocalDate parsed = LocalDate.parse("2026-05-20");
        LocalDateTime dt = LocalDateTime.parse("2026-05-20T14:30");
        System.out.println("parsed = " + parsed + ", dt = " + dt);

        section("5) Do NOT use Date / Calendar in new code");
        // java.util.Date is a thin wrapper over a long; mutable; toString
        // claims the local time zone; thread-unsafe with SimpleDateFormat.
        // Use java.time everywhere. If a legacy API hands you a Date,
        // immediately convert: date.toInstant().atZone(ZoneId.systemDefault())

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
