package Phase8_PracticalAPIs.FileIO;

import java.io.File;

/**
 * File I/O — Introduction
 * -----------------------
 * Java has TWO file APIs you should know:
 *
 *   1. java.io           - "classic" stream-based I/O, since 1.0.
 *                          Byte streams (InputStream/OutputStream) and
 *                          character streams (Reader/Writer). Still the
 *                          most common API for reading/writing files.
 *
 *   2. java.nio.file     - "new I/O" file API, since Java 7.
 *                          Path / Files utility methods that are
 *                          shorter, safer, and more flexible. PREFERRED
 *                          for new code.
 *
 * They INTEROPERATE: a Path can become a File (path.toFile()), and a
 * File can become a Path (file.toPath()).
 *
 *
 * The stream/reader/writer hierarchy
 * ----------------------------------
 *      InputStream / OutputStream     - bytes (8-bit)
 *      Reader / Writer                - characters (text)
 *      Buffered*                      - decorators that add buffering
 *      Data*                          - decorators for primitive types
 *      Object*                        - serialization
 *
 *
 * Why two APIs?
 * -------------
 *   - java.io is older, simpler, and ubiquitous in legacy code.
 *   - java.nio.file fixes a lot of papercuts in java.io:
 *       - Exceptions tell you WHY (NoSuchFileException vs vague IOException).
 *       - Cross-platform Path manipulation.
 *       - Atomic + copy/move/delete utilities.
 *       - File system events (WatchService).
 *       - Symlinks, attributes, ACLs.
 *
 *
 * Quick guide: which class for what
 * ---------------------------------
 *   "Just read a file as text"          - Files.readString(path)         [Java 11+]
 *   "Just write text"                    - Files.writeString(path, "...")
 *   "Read line by line"                  - Files.lines(path) or BufferedReader
 *   "Read bytes (image, binary, etc.)"   - Files.readAllBytes(path)
 *   "Random access in a big file"        - FileChannel / RandomAccessFile
 *   "Stream of files in a directory"     - Files.list / Files.walk
 *   "Be notified of changes"             - WatchService
 *
 *
 * Resource hygiene
 * ----------------
 * Anything that opens a file should be in a try-with-resources block:
 *
 *      try (var reader = Files.newBufferedReader(path)) {
 *          ...
 *      }
 *
 * The JVM does NOT close streams on GC promptly — leaks happen.
 *
 *
 * Character encodings
 * -------------------
 * Always specify the charset for text I/O. The platform default is a
 * landmine on Windows. Use StandardCharsets.UTF_8.
 *
 *
 * This file just shows the legacy `File` API to ground later examples.
 * The juicy stuff lives in the other files in this folder.
 */
public class FileIOIntroduction {

    public static void main(String[] args) {

        section("1) java.io.File — the OLD API");
        // File is a name handle, NOT an open file. It can refer to
        // something that doesn't even exist yet.
        File here = new File(".");
        System.out.println("abs path  = " + here.getAbsolutePath());
        System.out.println("exists?   = " + here.exists());
        System.out.println("isDir?    = " + here.isDirectory());
        System.out.println("free disk = " + here.getFreeSpace() + " bytes");

        section("2) Path is the modern equivalent — see FilesAndPaths.java");
        // We won't go deep here; this is the orientation file.

        section("3) The big picture: which class for which job");
        System.out.println("  read text         -> Files.readString / Files.lines");
        System.out.println("  read bytes        -> Files.readAllBytes");
        System.out.println("  read line-by-line -> Files.newBufferedReader");
        System.out.println("  random access     -> FileChannel / RandomAccessFile");
        System.out.println("  list directory    -> Files.list / Files.walk");
        System.out.println("  watch changes     -> WatchService");

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
