package Phase8_PracticalAPIs.FileIO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Walking the file system — Files.list / walk / find / lines
 * ----------------------------------------------------------
 * Stream-returning methods that let you traverse directories and process
 * files in a functional pipeline.
 * <p>
 *
 *   Files.list(dir)                 - direct children only
 *   Files.walk(dir [, maxDepth])    - dir and all descendants
 *   Files.find(dir, depth, matcher) - walk + filter via BiPredicate
 *   Files.lines(file [, charset])   - lines as a Stream<String>
 *   Files.walkFileTree(...)         - visitor pattern (lowest level)
 * <p>
 *
 * ALL of these return java.util.stream.Stream<...>. They consume an OS
 * file handle and MUST be closed — wrap in try-with-resources.
 * <p>
 *
 * walk vs walkFileTree
 * --------------------
 *   walk            - simpler, stream-friendly
 *   walkFileTree    - more powerful (FileVisitor with pre/post/visit
 *                     hooks, error handling per entry, skip/continue
 *                     subtree control). Better when you need to delete
 *                     a tree or react to errors mid-walk.
 */

public class FilesWalkAndList {

    public static void main(String[] args) throws IOException {

        Path root = Files.createTempDirectory("walk-demo-");
        // build:  root/a/b/c/leaf.txt and a few stray files
        Path nested = root.resolve("a/b/c");
        Files.createDirectories(nested);
        Files.writeString(nested.resolve("leaf.txt"), "leaf");
        Files.writeString(root.resolve("hello.txt"), "hi\nthere\n", StandardCharsets.UTF_8);
        Files.writeString(root.resolve("ignore.log"), "noise");
        Files.createDirectory(root.resolve("empty"));

        section("1) Files.list — direct children");
        try (Stream<Path> s = Files.list(root)) {
            s.forEach(p -> System.out.println("  " + p.getFileName()));
        }

        section("2) Files.walk — all descendants, unlimited depth");
        try (Stream<Path> s = Files.walk(root)) {
            s.forEach(p -> System.out.println("  " + root.relativize(p)));
        }

        section("3) Files.walk(maxDepth = 1)");
        try (Stream<Path> s = Files.walk(root, 1)) {
            s.forEach(p -> System.out.println("  " + root.relativize(p)));
        }

        section("4) Files.find — walk + BiPredicate (filter)");
        try (Stream<Path> s = Files.find(root, Integer.MAX_VALUE,
                (p, attr) -> attr.isRegularFile() && p.toString().endsWith(".txt"))) {
            s.forEach(p -> System.out.println("  matched .txt: " + root.relativize(p)));
        }

        section("5) Files.lines — process a file as a stream");
        try (Stream<String> lines = Files.lines(root.resolve("hello.txt"), StandardCharsets.UTF_8)) {
            long n = lines.filter(s -> !s.isBlank()).count();
            System.out.println("non-blank lines = " + n);
        }

        section("6) Word-count across the tree");
        try (Stream<Path> s = Files.walk(root)) {
            long totalWords = s
                    .filter(Files::isRegularFile)
                    .flatMap(p -> {
                        try { return Files.lines(p, StandardCharsets.UTF_8); }
                        catch (IOException e) { throw new RuntimeException(e); }
                    })
                    .flatMap(line -> Stream.of(line.split("\\s+")))
                    .filter(w -> !w.isEmpty())
                    .count();
            System.out.println("total words across tree = " + totalWords);
        }

        section("7) Recursive delete with walkFileTree (FileVisitor)");
        // Files.delete only works on empty dirs; to nuke a tree, sort
        // entries in reverse order or use FileVisitor.
        try (Stream<Path> s = Files.walk(root)) {
            s.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.delete(p); }
                catch (IOException ignored) {}
            });
        }
        System.out.println("deleted, exists? " + Files.exists(root));

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
