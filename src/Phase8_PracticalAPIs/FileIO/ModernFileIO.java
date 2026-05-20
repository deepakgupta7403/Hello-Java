package Phase8_PracticalAPIs.FileIO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * Modern File I/O — Java 11 → 21 conveniences
 * -------------------------------------------
 * The text-handling shortcuts most of your code should reach for first.
 *
 *
 * Java 11
 * -------
 *   Files.readString(path[, charset])
 *   Files.writeString(path, charSequence[, charset][, options...])
 *   String.lines() / String.repeat() / String.isBlank() / String.strip()
 *
 *
 * Java 12+
 * --------
 *   String.indent(n) / String.transform(Function<String,R>)
 *
 *
 * Java 13+
 * --------
 *   String.formatted(args)
 *   Text blocks ("""...""")
 *
 *
 * Java 16+
 * --------
 *   Stream.toList() — convenient over Collectors.toList()
 *
 *
 * Java 17+
 * --------
 *   Sealed type hierarchies + pattern matching make the surface around
 *   reading "different shapes of records" cleaner.
 *
 *
 * Java 21 hot tip
 * ---------------
 *   Combine Files.lines + Stream.gather(...) (preview) for windowed
 *   text processing. For now we'll do it with the stable API.
 */

public class ModernFileIO {

    public static void main(String[] args) throws IOException {

        section("1) Files.writeString / readString — Java 11");
        Path tmp = Files.createTempFile("modern-", ".md");
        Files.writeString(tmp, """
                # Modern Java I/O
                Lines like this.
                Another line.
                """, StandardCharsets.UTF_8);
        String all = Files.readString(tmp, StandardCharsets.UTF_8);
        System.out.println("read:\n" + all);

        section("2) Files.write(List<String>) — line-oriented");
        Files.write(tmp, List.of("alpha", "beta", "gamma"),
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        for (String s : Files.readAllLines(tmp, StandardCharsets.UTF_8)) {
            System.out.println("  " + s);
        }

        section("3) Files.lines + Stream + toList()");
        try (Stream<String> s = Files.lines(tmp, StandardCharsets.UTF_8)) {
            List<String> upper = s.map(String::toUpperCase).toList();   // Java 16+
            System.out.println(upper);
        }

        section("4) String.lines() — process an already-loaded string");
        String content = """
                one
                two
                three
                """;
        long n = content.lines().count();
        System.out.println("string had " + n + " lines");

        section("5) Append mode and createNew");
        Path append = Files.createTempFile("modern-append-", ".log");
        Files.writeString(append, "line 1\n", StandardCharsets.UTF_8);
        Files.writeString(append, "line 2\n", StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
        System.out.println(Files.readString(append));

        section("6) Files.mismatch — byte-compare two files (Java 12+)");
        Path a = Files.createTempFile("a-", ".bin");
        Path b = Files.createTempFile("b-", ".bin");
        Files.writeString(a, "hello world");
        Files.writeString(b, "hello WORLD");
        long mismatch = Files.mismatch(a, b);            // -1 if equal, else first differing byte
        System.out.println("first differing byte = " + mismatch);

        section("7) Files.copy with InputStream / OutputStream — handy bridge");
        // e.g., piping an HTTP response straight to disk:
        // Files.copy(httpResponse.body(), destPath);

        Files.deleteIfExists(tmp);
        Files.deleteIfExists(append);
        Files.deleteIfExists(a);
        Files.deleteIfExists(b);
        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
