package Phase8_PracticalAPIs.FileIO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;

/**
 * java.nio.file — Path and Files
 * ------------------------------
 * The modern Java file API (since 7). Two main types:
 *
 *      Path       - an immutable file-system path. Replaces File.
 *                   Made via Path.of(...) or Paths.get(...).
 *
 *      Files      - a class of STATIC utility methods that DO the work:
 *                   read, write, copy, move, delete, walk, attributes,
 *                   streams, etc. ~90% of file code lives in here.
 *
 *
 * Why prefer this over java.io.File?
 * ----------------------------------
 *   - Better error messages (NoSuchFileException, NotDirectoryException,
 *     AccessDeniedException ... vs vague IOException).
 *   - Symbolic links handled explicitly.
 *   - Attributes / permissions / ACLs are first-class.
 *   - Atomic copy / move / delete.
 *   - Streams (Files.lines, Files.list, Files.walk).
 *   - File-system events (WatchService).
 *   - Pluggable file systems (ZIP file as a virtual FS).
 *
 *
 * Common Path operations
 * ----------------------
 *      Path.of("a", "b", "c")        - construct from segments
 *      p.getFileName()               - just the leaf name
 *      p.getParent()                 - directory
 *      p.getRoot()                   - root (drive on Windows)
 *      p.resolve("file.txt")         - append a child path
 *      p.relativize(other)           - compute the path from p to other
 *      p.normalize()                 - collapse "." and ".." segments
 *      p.toAbsolutePath()            - relative -> absolute
 *      p.toRealPath()                - resolves symlinks too
 *      p.startsWith / endsWith       - structural matching
 *
 *
 * Common Files operations
 * -----------------------
 *      Files.createFile / createDirectory(/ies) / createTempFile / Directory
 *      Files.exists / isRegularFile / isDirectory / isReadable / isWritable
 *      Files.size                    - bytes
 *      Files.readAllBytes / readString / readAllLines
 *      Files.write / writeString
 *      Files.copy / move / delete
 *      Files.readAttributes / setLastModifiedTime / setPosixFilePermissions
 *      Files.list / walk / lines     - return Streams!
 *      Files.newBufferedReader / newBufferedWriter
 */

public class FilesAndPaths {

    public static void main(String[] args) throws IOException {

        section("1) Constructing Paths");
        Path p = Path.of("src", "Basics", "FileIO", "FilesAndPaths.java");
        Path p2 = Paths.get("/var", "log", "syslog");
        System.out.println("p  = " + p);
        System.out.println("p2 = " + p2);

        section("2) Useful Path methods");
        Path file = Path.of("home/alice/docs/notes.txt");
        System.out.println("fileName = " + file.getFileName());
        System.out.println("parent   = " + file.getParent());
        System.out.println("nameCount= " + file.getNameCount());
        System.out.println("startsWith home? " + file.startsWith("home"));

        Path absHere = Path.of(".").toAbsolutePath().normalize();
        System.out.println("absolute here = " + absHere);

        Path next = absHere.resolve("README.md");
        System.out.println("resolved README = " + next);
        System.out.println("relativize from /tmp -> here = " + Path.of("/tmp").relativize(absHere));

        section("3) Files.write / readString / readAllLines / writeString");
        Path tmp = Files.createTempFile("fp-", ".txt");
        Files.writeString(tmp, "first line\nsecond line\n", StandardCharsets.UTF_8);
        System.out.println("readString:\n" + Files.readString(tmp));
        for (String line : Files.readAllLines(tmp)) System.out.println("  | " + line);

        section("4) Append mode and options");
        Files.writeString(tmp, "appended\n", StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
        System.out.println("after append:\n" + Files.readString(tmp));

        section("5) Attributes");
        BasicFileAttributes attr = Files.readAttributes(tmp, BasicFileAttributes.class);
        System.out.println("size            = " + attr.size());
        System.out.println("creationTime    = " + attr.creationTime());
        System.out.println("lastModified    = " + attr.lastModifiedTime());
        System.out.println("isRegularFile   = " + attr.isRegularFile());

        // touch the modified time
        Files.setLastModifiedTime(tmp, FileTime.fromMillis(System.currentTimeMillis() + 60_000));
        System.out.println("touched mtime   = " + Files.getLastModifiedTime(tmp));

        section("6) Copy / move / delete");
        Path copy = Path.of(tmp.getParent().toString(), "fp-copy.txt");
        Path moved = Path.of(tmp.getParent().toString(), "fp-moved.txt");
        Files.copy(tmp, copy, StandardCopyOption.REPLACE_EXISTING);
        Files.move(copy, moved, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("exists original? " + Files.exists(tmp));
        System.out.println("exists copy?     " + Files.exists(copy));     // false (moved)
        System.out.println("exists moved?    " + Files.exists(moved));    // true

        section("7) Cleanup");
        Files.deleteIfExists(tmp);
        Files.deleteIfExists(copy);
        Files.deleteIfExists(moved);

        section("8) Files.newBufferedReader — modern way to readLine");
        Path big = Files.createTempFile("fp-big-", ".txt");
        Files.write(big, List.of("alpha", "beta", "gamma"));
        try (var r = Files.newBufferedReader(big, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) System.out.println("  " + line);
        }
        Files.deleteIfExists(big);

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
