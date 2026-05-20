package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Streams + File I/O (java.nio.file.Files)
 * ----------------------------------------
 * The java.nio.file.Files class exposes several stream-returning methods.
 * They let you process files with the same declarative style you use for
 * collections - and crucially they STREAM lazily, so you can process a
 * multi-gigabyte file with a tiny memory footprint.
 *
 *
 * Stream-Returning Methods on Files
 * ---------------------------------
 *      Files.lines(Path)                  Stream&lt;String&gt; - one element per line
 *      Files.lines(Path, Charset)
 *      Files.list(Path)                    Stream&lt;Path&gt;   - children of one dir
 *      Files.walk(Path[, maxDepth])        Stream&lt;Path&gt;   - recursive tree walk
 *      Files.find(Path, depth, biPredicate) recursive with a filter
 *
 *
 * Why You Should Use try-with-resources
 * -------------------------------------
 * These streams hold OS handles (file descriptors). They implement
 * AutoCloseable; ALWAYS wrap them in try-with-resources so the handle is
 * released even if the pipeline throws.
 *
 *      try (Stream&lt;String&gt; lines = Files.lines(path)) {
 *          lines.filter(...).forEach(...);
 *      }
 *
 *
 * Writing Files - the Counterpart
 * -------------------------------
 *      Files.write(Path, Iterable&lt;String&gt;)            - whole file at once
 *      Files.write(Path, byte[], OpenOption...)
 *      Files.writeString(Path, CharSequence)           Java 11+
 *      Files.newBufferedWriter(Path)                    streamable BufferedWriter
 *
 *
 * Demo Below
 * ----------
 * We write a small CSV-like file in /tmp (or the OS temp dir), read it
 * back as a Stream, do a couple of analyses, then delete the file.
 */

public class StreamFileIO {

    public static void main(String[] args) throws IOException {

        section("1) Write a sample file");
        Path file = Files.createTempFile("hellojava-stream-", ".csv");
        Files.write(file, List.of(
                "name,dept,salary",
                "Alice,ENG,90000",
                "Bob,ENG,85000",
                "Carol,PM,110000",
                "Dave,ENG,95000",
                "Eve,PM,105000",
                "Fran,HR,80000"
        ), StandardCharsets.UTF_8);
        System.out.println("wrote " + file);

        section("2) Files.lines - process line by line as a Stream");
        try (Stream<String> lines = Files.lines(file)) {
            long count = lines.skip(1).count();           // skip header
            System.out.println("data rows = " + count);
        }

        section("3) Stream pipeline against the file");
        try (Stream<String> lines = Files.lines(file)) {
            double engTotal = lines
                    .skip(1)
                    .map(s -> s.split(","))
                    .filter(cols -> cols[1].equals("ENG"))
                    .mapToDouble(cols -> Double.parseDouble(cols[2]))
                    .sum();
            System.out.println("total ENG salary = " + engTotal);
        }

        section("4) Group by department via Collectors");
        try (Stream<String> lines = Files.lines(file)) {
            Map<String, List<String>> byDept = lines
                    .skip(1)
                    .map(s -> s.split(","))
                    .collect(Collectors.groupingBy(
                            cols -> cols[1],
                            Collectors.mapping(cols -> cols[0], Collectors.toList())));
            byDept.forEach((d, names) -> System.out.println(d + " -> " + names));
        }

        section("5) Files.list - children of one directory (top-level only)");
        Path parent = file.getParent();
        try (Stream<Path> kids = Files.list(parent)) {
            long count = kids
                    .filter(p -> p.getFileName().toString().startsWith("hellojava-stream-"))
                    .count();
            System.out.println("temp files matching prefix = " + count);
        }

        section("6) Files.walk - recursive walk (BFS-like) - depth 1 only here");
        try (Stream<Path> walk = Files.walk(parent, 1)) {
            walk.limit(5).forEach(p -> System.out.println("  " + p.getFileName()));
        }

        section("7) Writing - append more rows with newBufferedWriter");
        try (var bw = Files.newBufferedWriter(file,
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            bw.write("Greg,HR,82000");
            bw.newLine();
            bw.write("Hank,PM,108000");
            bw.newLine();
        }
        try (Stream<String> lines = Files.lines(file)) {
            System.out.println("rows after append = " + lines.count());
        }

        section("8) Files.writeString (Java 11+) - quick whole-file write");
        Path notes = Files.createTempFile("hellojava-stream-notes-", ".txt");
        Files.writeString(notes, "first line\nsecond line\n");
        try (Stream<String> lines = Files.lines(notes)) {
            lines.forEach(s -> System.out.println("  read: " + s));
        }

        section("9) Cleanup");
        Files.deleteIfExists(file);
        Files.deleteIfExists(notes);
        System.out.println("temp files deleted");

        // OUTPUT (file paths vary by OS)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
