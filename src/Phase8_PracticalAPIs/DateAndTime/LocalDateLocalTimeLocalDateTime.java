package Phase8_PracticalAPIs.DateAndTime;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Stream;

/**
 * LocalDate / LocalTime / LocalDateTime
 * -------------------------------------
 * Three "local" types — no zone info, no offset. Use them whenever the
 * time zone is implicit or irrelevant (a birthday, an alarm, a calendar
 * entry that doesn't travel across time zones).
 * <p>
 *
 * Construction
 * ------------
 *      LocalDate.now()                   - today, system default zone
 *      LocalDate.of(2026, 5, 20)
 *      LocalDate.of(2026, Month.MAY, 20)
 *      LocalDate.parse("2026-05-20")     - ISO-8601
 *      LocalTime.of(14, 30)
 *      LocalTime.of(14, 30, 15, 123_000_000)
 *      LocalDateTime.of(date, time)
 *      LocalDateTime.of(2026, 5, 20, 14, 30)
 * <p>
 *
 * Manipulation
 * ------------
 *   .plusDays(n) / minusDays / plusWeeks / plusMonths / plusYears
 *   .with(field, value)                   - immutable replace
 *   .withMonth(...) / withDayOfMonth(...)
 *   .with(TemporalAdjusters.firstDayOfMonth())
 *   .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
 * <p>
 *
 * Querying
 * --------
 *   .getYear(), .getMonth(), .getDayOfMonth(), .getDayOfWeek(), .getDayOfYear()
 *   .lengthOfMonth(), .lengthOfYear(), .isLeapYear()
 *   .isBefore(other), .isAfter(other), .equals(other)
 * <p>
 *
 * Differences
 * -----------
 *   long diff = ChronoUnit.DAYS.between(start, end);
 *   Period p   = start.until(end);                  // returns years/months/days
 * <p>
 *
 * Streams of dates
 * ----------------
 *      start.datesUntil(end)           - Stream<LocalDate>
 *      start.datesUntil(end, Period.ofMonths(1))   - stride
 */

public class LocalDateLocalTimeLocalDateTime {

    public static void main(String[] args) {

        section("1) Construction");
        LocalDate d  = LocalDate.of(2026, Month.MAY, 20);
        LocalTime t  = LocalTime.of(14, 30);
        LocalDateTime dt = LocalDateTime.of(d, t);
        System.out.println("date = " + d);
        System.out.println("time = " + t);
        System.out.println("dt   = " + dt);

        section("2) Arithmetic — immutable");
        LocalDate nextWeek = d.plusWeeks(1);
        LocalDate twoMonthsAgo = d.minusMonths(2);
        LocalDateTime later = dt.plusHours(5).plusMinutes(15);
        System.out.println("nextWeek    = " + nextWeek);
        System.out.println("twoMonthsAgo= " + twoMonthsAgo);
        System.out.println("later       = " + later);

        section("3) Querying");
        System.out.println("day-of-week    = " + d.getDayOfWeek());
        System.out.println("day-of-year    = " + d.getDayOfYear());
        System.out.println("month length   = " + d.lengthOfMonth());
        System.out.println("is leap year?  = " + d.isLeapYear());

        section("4) Differences");
        LocalDate end = LocalDate.of(2026, 12, 31);
        long days = ChronoUnit.DAYS.between(d, end);
        java.time.Period p = d.until(end);
        System.out.println("days between           = " + days);
        System.out.println("period between         = " + p);

        section("5) TemporalAdjusters — natural-language adjustments");
        LocalDate firstOfMonth = d.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastOfMonth  = d.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate nextFriday   = d.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        LocalDate lastOfYear   = d.with(TemporalAdjusters.lastDayOfYear());
        System.out.println("first of month = " + firstOfMonth);
        System.out.println("last of month  = " + lastOfMonth);
        System.out.println("next Friday    = " + nextFriday);
        System.out.println("last of year   = " + lastOfYear);

        section("6) Custom adjuster — last weekday of the month");
        LocalDate lastWeekday = d.with(temp -> {
            LocalDate ld = LocalDate.from(temp);
            ld = ld.with(TemporalAdjusters.lastDayOfMonth());
            while (ld.getDayOfWeek() == DayOfWeek.SATURDAY || ld.getDayOfWeek() == DayOfWeek.SUNDAY) {
                ld = ld.minusDays(1);
            }
            return ld;
        });
        System.out.println("last weekday   = " + lastWeekday);

        section("7) Stream of dates (Java 9+)");
        try (Stream<LocalDate> s = d.datesUntil(d.plusDays(7))) {
            s.forEach(x -> System.out.println("  " + x + " (" + x.getDayOfWeek() + ")"));
        }

        section("8) Comparison and equality");
        System.out.println("d.equals(d2)   = " + d.equals(LocalDate.of(2026, 5, 20)));
        System.out.println("d.isBefore     = " + d.isBefore(end));
        System.out.println("compareTo      = " + d.compareTo(end));    // < 0

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
