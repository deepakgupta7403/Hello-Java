package Basics.DateAndTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * DateTimeFormatter
 * -----------------
 * Parse strings into date/time values and format them back. THREAD-SAFE
 * and IMMUTABLE — share instances freely. Replaces the legacy (and
 * broken) SimpleDateFormat.
 *
 *
 * Three ways to get a formatter
 * -----------------------------
 *   1. CONSTANTS — DateTimeFormatter.ISO_LOCAL_DATE etc. for ISO-8601.
 *   2. PATTERN   — DateTimeFormatter.ofPattern("dd/MM/yyyy")
 *   3. BUILDER   — new DateTimeFormatterBuilder()... for full control.
 *
 *
 * Common pattern letters
 * ----------------------
 *   y / u   year (u allows negative years)
 *   M       month-of-year   (MM=07, MMM=Jul, MMMM=July)
 *   d       day-of-month
 *   E       day-of-week     (E=Mon, EEEE=Monday)
 *   H / h   hour 0-23 / 1-12
 *   m       minute
 *   s       second
 *   S / n   fraction-of-second / nano
 *   a       AM/PM
 *   z       zone abbreviation
 *   V       zone id
 *   x / X   offset (+0530 / +05:30)
 *
 *
 * Locale matters
 * --------------
 * Month and day names depend on locale.
 *      ofPattern("MMM d", Locale.US)        -> Jul 4
 *      ofPattern("MMM d", Locale.GERMAN)    -> Jul 4
 *
 *
 * Tips
 * ----
 *   - Always pin the Locale for output that crosses borders.
 *   - For text that humans see, prefer FormatStyle (LONG/MEDIUM/SHORT).
 *   - For machine interchange, prefer ISO constants.
 */

public class DateTimeFormatterDemo {

    public static void main(String[] args) {

        section("1) ISO constants — machine-friendly");
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        System.out.println(today.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        System.out.println(today.format(DateTimeFormatter.BASIC_ISO_DATE));      // YYYYMMDD

        section("2) ofPattern — custom");
        DateTimeFormatter eu = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter readable = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy");
        System.out.println(today.format(eu));
        System.out.println(today.format(readable));

        section("3) Locale matters");
        DateTimeFormatter en = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ENGLISH);
        DateTimeFormatter fr = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH);
        DateTimeFormatter hi = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.forLanguageTag("hi"));
        System.out.println("en: " + today.format(en));
        System.out.println("fr: " + today.format(fr));
        System.out.println("hi: " + today.format(hi));

        section("4) Localized FormatStyle");
        DateTimeFormatter shortFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.US);
        DateTimeFormatter longFmt  = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.US);
        System.out.println("SHORT US = " + today.format(shortFmt));
        System.out.println("LONG  US = " + today.format(longFmt));

        section("5) parse() — strings to date types");
        LocalDate parsedIso = LocalDate.parse("2026-05-20");
        LocalDate parsedEu  = LocalDate.parse("20/05/2026", eu);
        System.out.println("parsedIso = " + parsedIso);
        System.out.println("parsedEu  = " + parsedEu);

        section("6) Strict parsing via builder");
        // Year must be exactly 4 digits, no surprise width.
        DateTimeFormatter strict = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-')
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(ChronoField.DAY_OF_MONTH, 2)
                .toFormatter();
        System.out.println("strict = " + LocalDate.parse("2026-05-20", strict));

        section("7) ZonedDateTime with offset / zone in the pattern");
        ZonedDateTime z = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter zoned = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss zzz XXX");
        System.out.println(z.format(zoned));

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
