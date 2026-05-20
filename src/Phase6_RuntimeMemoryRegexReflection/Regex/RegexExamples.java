package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.regex.Pattern;

/**
 * Real-World Regex Examples
 * -------------------------
 * A cookbook of ready-to-copy regex patterns. Each one has the regex in a
 * compiled Pattern constant, a short note on what it accepts and rejects,
 * and a few test cases printed.
 *
 *
 * Warning - "Validation" is a Trade-Off
 * -------------------------------------
 * There is no single regex that "perfectly" validates real emails or URLs
 * (RFC 5321 / 3986 are intricate). The patterns here favour SIMPLE and
 * READABLE over RFC-perfect. For high-stakes validation, prefer a parser
 * library (Apache Commons Validator, Hibernate Validator, etc.) and use
 * regex for normalisation / extraction.
 */

public class RegexExamples {

    // 1) Email - simple, practical
    //    user@domain.tld with letters/digits/._%+- in the local part,
    //    letters/digits/.- in the domain, a top-level of 2+ letters.
    static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // 2) US phone - flexible, accepts "(555) 867-5309", "555-867-5309",
    //    "555.867.5309", or "5558675309".
    static final Pattern US_PHONE = Pattern.compile(
            "^\\(?(\\d{3})\\)?[\\s.-]?(\\d{3})[\\s.-]?(\\d{4})$"
    );

    // 3) Indian mobile - 10 digits, optional +91 / 0 prefix.
    static final Pattern IN_MOBILE = Pattern.compile(
            "^(?:\\+91[\\s-]?|0)?[6-9]\\d{9}$"
    );

    // 4) URL - http/https, optional port, optional path. Not RFC-strict.
    static final Pattern URL = Pattern.compile(
            "^https?://[\\w.-]+(:\\d+)?(/[\\w./?%&=#-]*)?$"
    );

    // 5) IPv4 - each octet 0..255 (no leading-zero check).
    static final Pattern IPV4 = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}" +
            "(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$"
    );

    // 6) Strong password - 8+ chars, at least one lower, upper, digit, special.
    static final Pattern STRONG_PASSWORD = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    // 7) ISO-8601 date YYYY-MM-DD with light range checks.
    static final Pattern ISO_DATE = Pattern.compile(
            "^\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01])$"
    );

    // 8) Hex colour - "#RGB", "#RRGGBB", optional "#"
    static final Pattern HEX_COLOR = Pattern.compile(
            "^#?(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6})$"
    );

    // 9) Slug - lowercase letters, digits, dashes. No leading/trailing dash.
    static final Pattern SLUG = Pattern.compile(
            "^[a-z0-9]+(?:-[a-z0-9]+)*$"
    );

    // 10) Whitespace collapse - reduce runs of whitespace to a single space.
    static final Pattern WS = Pattern.compile("\\s+");

    public static void main(String[] args) {

        section("1) Email");
        check(EMAIL, "deepak@example.com", "deepak.gupta@example.co.in",
                     "bad-email", "@no-local.com", "no-at.example.com");

        section("2) US phone");
        check(US_PHONE, "(555) 867-5309", "555-867-5309", "555.867.5309",
                        "5558675309", "555-1234");

        section("3) Indian mobile");
        check(IN_MOBILE, "+91 9876543210", "09876543210", "9876543210",
                         "1234567890", "+91 5876543210");

        section("4) URL");
        check(URL, "http://example.com", "https://example.com:8080/path?x=1",
                   "ftp://example.com", "example.com");

        section("5) IPv4");
        check(IPV4, "127.0.0.1", "255.255.255.255", "0.0.0.0",
                    "256.0.0.1", "1.2.3", "abc.def.ghi.jkl");

        section("6) Strong password");
        check(STRONG_PASSWORD, "Aa1@aaaa", "weakpwd", "STRONG1@", "Strong@");

        section("7) ISO date");
        check(ISO_DATE, "2026-05-19", "2026-13-01", "2026-05-32",
                        "2026/05/19", "20260519");

        section("8) Hex color");
        check(HEX_COLOR, "#FFA500", "FFA500", "#abc", "abcd", "#GGG");

        section("9) Slug");
        check(SLUG, "hello-world", "java-21-release", "Hello-World",
                    "-leading", "trailing-", "double--dash");

        section("10) Whitespace cleanup");
        String dirty = "  hello\t\tworld\n\nfrom    Java  ";
        String clean = WS.matcher(dirty.trim()).replaceAll(" ");
        System.out.println("dirty = \"" + dirty + "\"");
        System.out.println("clean = \"" + clean + "\"");

        section("11) CSV with optional quoted fields - a quick example");
        // Field is either "..." or non-comma non-quote chars
        Pattern csv = Pattern.compile("\"([^\"]*)\"|([^,]+)");
        String line = "alpha,\"hello, world\",beta,,\"quoted\"";
        java.util.List<String> fields = csv.matcher(line)
                .results()
                .map(r -> r.group(1) != null ? r.group(1) : r.group(2))
                .toList();
        System.out.println("fields = " + fields);

        // SAMPLE OUTPUT (matches inline comments)
        // ====== 1) Email ======
        // deepak@example.com           -> true
        // deepak.gupta@example.co.in   -> true
        // bad-email                    -> false
        // @no-local.com                -> false
        // no-at.example.com            -> false
        // ...
    }

    private static void check(Pattern p, String... inputs) {
        for (String in : inputs) {
            System.out.printf("  %-32s -> %s%n", "\"" + in + "\"", p.matcher(in).matches());
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
