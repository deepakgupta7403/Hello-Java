package Phase8_PracticalAPIs.FileIO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * FileChannel — Random Access, Memory Mapping, Locks
 * --------------------------------------------------
 * FileChannel is the NIO answer to RandomAccessFile and a bunch of OS-level
 * features:
 *
 *   - Random access (seek with position()).
 *   - Bulk reads/writes via ByteBuffer.
 *   - Direct memory transfers between channels (transferTo / transferFrom).
 *   - MEMORY MAPPING — map a region of a file into memory; treat it
 *     like a giant array.
 *   - File LOCKS — advisory, OS-level locking for shared / exclusive
 *     access.
 *
 *
 * How to obtain one
 * -----------------
 *      FileChannel.open(path, options...)             // preferred
 *      new RandomAccessFile(file, "rw").getChannel()  // legacy
 *
 *
 * The mental model
 * ----------------
 *   - A channel has a POSITION (where the next read/write happens).
 *   - You move bytes between the channel and a ByteBuffer.
 *   - ByteBuffer has its own position / limit / capacity. Don't forget
 *     to call buf.flip() between writing-into-the-buffer and reading-
 *     out-of-it.
 *
 *
 * Memory-mapped files
 * -------------------
 * For very large files, mapping a region into memory lets the OS handle
 * paging. Lightning fast reads, but:
 *   - The mapping isn't released until GC; explicit clean-up is JVM-
 *     specific (sun.misc.Unsafe, etc.) — avoid mapping huge files in a
 *     loop.
 *   - On Windows, the file may stay locked until the JVM exits.
 *
 *
 * When to use FileChannel
 * -----------------------
 *   - Random-access updates of a large file.
 *   - High-throughput copy with transferTo (zero-copy on many OSes).
 *   - Memory mapping for binary search / index access.
 *   - Cross-process file locking.
 */

public class FileChannelDemo {

    public static void main(String[] args) throws IOException {

        Path src = Files.createTempFile("ch-src-", ".bin");
        Files.writeString(src, "Hello, NIO! Random access here.\n", StandardCharsets.UTF_8);

        section("1) Open a FileChannel for read+write");
        try (FileChannel ch = FileChannel.open(src,
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            System.out.println("size = " + ch.size() + ", pos = " + ch.position());

            section("2) Read into a ByteBuffer");
            ByteBuffer buf = ByteBuffer.allocate(16);
            int n = ch.read(buf);                       // fills buf up to limit
            buf.flip();                                 // prepare for reading
            byte[] read = new byte[n];
            buf.get(read);
            System.out.println("read " + n + " bytes: '" + new String(read, StandardCharsets.UTF_8) + "'");

            section("3) Random-access write at position 7");
            ch.position(7);                              // seek
            ch.write(ByteBuffer.wrap("REPLACED".getBytes(StandardCharsets.UTF_8)));
        }
        System.out.println("after edit: '" + Files.readString(src).strip() + "'");

        section("4) transferTo — efficient channel-to-channel copy");
        Path dest = Files.createTempFile("ch-dest-", ".bin");
        try (FileChannel in  = FileChannel.open(src, StandardOpenOption.READ);
             FileChannel out = FileChannel.open(dest, StandardOpenOption.WRITE)) {
            long copied = in.transferTo(0, in.size(), out);
            System.out.println("transferTo copied " + copied + " bytes");
        }

        section("5) Memory-map a file as a ByteBuffer");
        try (FileChannel ch = FileChannel.open(src, StandardOpenOption.READ)) {
            MappedByteBuffer mapped = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size());
            byte[] first10 = new byte[10];
            mapped.get(first10);
            System.out.println("first 10 mapped bytes = '"
                    + new String(first10, StandardCharsets.UTF_8) + "'");
        }

        section("6) RandomAccessFile — the legacy alternative");
        try (RandomAccessFile raf = new RandomAccessFile(src.toFile(), "r")) {
            raf.seek(0);
            byte[] head = new byte[5];
            raf.readFully(head);
            System.out.println("RAF first 5 = '" + new String(head, StandardCharsets.UTF_8) + "'");
        }

        section("7) Advisory file lock (exclusive)");
        try (FileChannel ch = FileChannel.open(src, StandardOpenOption.READ, StandardOpenOption.WRITE);
             var lock = ch.lock()) {                       // FileLock implements AutoCloseable
            System.out.println("held exclusive lock = " + lock.isValid());
        }

        Files.deleteIfExists(src);
        Files.deleteIfExists(dest);
        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
