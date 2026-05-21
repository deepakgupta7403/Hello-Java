package Phase8_PracticalAPIs.FileIO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Buffered Streams
 * ----------------
 * Reading or writing one byte (or char) at a time hits the OS for EVERY
 * call. A buffer slurps a big chunk at once, then serves it from
 * memory. Cheap, easy, ~10-50x faster for byte-by-byte loops.
 * <p>
 *
 * The four classes
 * ----------------
 *   BufferedInputStream  wraps  InputStream
 *   BufferedOutputStream wraps  OutputStream
 *   BufferedReader       wraps  Reader
 *   BufferedWriter       wraps  Writer
 * <p>
 *
 * Pattern
 * -------
 *      try (var in = new BufferedInputStream(new FileInputStream(file))) {
 *          // ...byte-by-byte reads are now cheap
 *      }
 * <p>
 *
 * For text the modern shortcut is:
 * <p>
 *
 *      try (BufferedReader r = Files.newBufferedReader(path, UTF_8)) {
 *          ...
 *      }
 * <p>
 *
 * Useful extras
 * -------------
 *   BufferedReader.readLine()       - read up to the next \n / \r\n
 *   BufferedReader.lines()          - Stream<String> of lines (Java 8+)
 *   BufferedWriter.newLine()        - platform-correct line separator
 * <p>
 *
 * When NOT to bother
 * ------------------
 *   - You're already reading in big chunks (e.g. read(buf) with 8KB+).
 *   - You're using Files.readAllBytes / readString — they buffer internally.
 *   - Performance isn't a concern (small files).
 * <p>
 *
 * flush vs close
 * --------------
 * close() flushes for you. Calling flush() before close is needed only
 * if downstream consumers should see partial output (logs, sockets).
 */

public class BufferedStreams {

    public static void main(String[] args) throws IOException {

        Path tmp = Files.createTempFile("buf-demo-", ".txt");

        section("1) BufferedWriter — line-by-line text output");
        try (BufferedWriter w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            for (int i = 1; i <= 5; i++) {
                w.write("line " + i);
                w.newLine();                       // \n or \r\n per platform
            }
        }

        section("2) BufferedReader — readLine loop");
        try (BufferedReader r = Files.newBufferedReader(tmp, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) System.out.println("  " + line);
        }

        section("3) BufferedReader.lines() — Stream<String>");
        try (BufferedReader r = Files.newBufferedReader(tmp, StandardCharsets.UTF_8)) {
            String upper = r.lines().map(String::toUpperCase).collect(Collectors.joining(" | "));
            System.out.println("joined upper = " + upper);
        }

        section("4) BufferedInputStream / OutputStream — bytes, fast");
        Path bin = Files.createTempFile("buf-bin-", ".bin");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(bin.toFile()))) {
            for (int i = 0; i < 100_000; i++) out.write(i & 0xff);
        }
        long total = 0;
        try (InputStream in = new BufferedInputStream(new FileInputStream(bin.toFile()))) {
            int b;
            while ((b = in.read()) != -1) total += b;
        }
        System.out.println("read sum = " + total + " (would be glacial without buffering)");

        section("5) Micro-benchmark — unbuffered vs buffered byte-by-byte");
        Path big = Files.createTempFile("buf-big-", ".bin");
        Files.write(big, new byte[2_000_000]);
        long t0 = System.nanoTime();
        try (InputStream in = new FileInputStream(big.toFile())) {
            while (in.read() != -1) ;
        }
        long unbuf = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        try (InputStream in = new BufferedInputStream(new FileInputStream(big.toFile()))) {
            while (in.read() != -1) ;
        }
        long buffed = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("unbuffered byte-by-byte : " + unbuf + " ms");
        System.out.println("buffered  byte-by-byte : " + buffed + " ms");

        // Cleanup
        Files.deleteIfExists(tmp);
        Files.deleteIfExists(bin);
        Files.deleteIfExists(big);
        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
