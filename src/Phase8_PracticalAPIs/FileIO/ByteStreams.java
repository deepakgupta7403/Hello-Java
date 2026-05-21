package Phase8_PracticalAPIs.FileIO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Byte Streams — InputStream / OutputStream
 * -----------------------------------------
 * The grandparents of Java I/O. They read and write RAW BYTES, one or
 * many at a time. Used for non-textual files (images, audio, archives)
 * or as the lower layer beneath character streams.
 * <p>
 *
 * InputStream — read bytes
 * ------------------------
 *      int read()                  -> next byte 0..255, or -1 at EOF
 *      int read(byte[] b)          -> fill the array; return how many were read
 *      int read(byte[] b, off,len) -> fill part of the array
 *      long skip(long n)
 *      void close()
 *      byte[] readAllBytes()       (Java 9+)
 *      long transferTo(OutputStream)
 * <p>
 *
 * OutputStream — write bytes
 * --------------------------
 *      void write(int b)           -> writes the low 8 bits
 *      void write(byte[] b)
 *      void write(byte[] b, off, len)
 *      void flush()
 *      void close()
 * <p>
 *
 * Concrete file-flavoured impls
 * -----------------------------
 *      FileInputStream   - read bytes from a file.
 *      FileOutputStream  - write bytes (optional append mode).
 *      ByteArrayInputStream / ByteArrayOutputStream - memory streams.
 * <p>
 *
 * Best practices
 * --------------
 *   - Always try-with-resources.
 *   - Use read(byte[]) with a buffer of ~8 KiB — read() one byte at a
 *     time is dramatically slower.
 *   - Better still, wrap with a BufferedInputStream (see BufferedStreams.java).
 *   - For text, use Reader/Writer, NOT InputStream/OutputStream + new String(...).
 * <p>
 *
 * Java 9+ shortcuts
 * -----------------
 *      Files.readAllBytes(path)
 *      Files.write(path, byte[] data)
 *      InputStream.readAllBytes()
 *      InputStream.transferTo(OutputStream)
 */

public class ByteStreams {

    public static void main(String[] args) throws IOException {

        Path tmp = Files.createTempFile("byte-demo-", ".bin");

        section("1) FileOutputStream — write raw bytes");
        try (OutputStream out = new FileOutputStream(tmp.toFile())) {
            for (int i = 0; i < 10; i++) out.write(i);
            out.write(new byte[]{65, 66, 67});            // ABC
        }
        System.out.println("wrote " + Files.size(tmp) + " bytes");

        section("2) FileInputStream — read byte by byte");
        try (InputStream in = new FileInputStream(tmp.toFile())) {
            int b;
            while ((b = in.read()) != -1) {
                System.out.print(b + " ");
            }
            System.out.println();
        }

        section("3) FileInputStream — read into a buffer (fast)");
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(tmp.toFile())) {
            int n = in.read(buf);                          // up to buf.length bytes
            System.out.println("read " + n + " bytes; first 5 = "
                    + buf[0] + " " + buf[1] + " " + buf[2] + " " + buf[3] + " " + buf[4]);
        }

        section("4) Files.readAllBytes — modern one-liner");
        byte[] all = Files.readAllBytes(tmp);
        System.out.println("readAllBytes length = " + all.length);

        section("5) transferTo — copy one stream to another");
        Path dest = Files.createTempFile("byte-dest-", ".bin");
        try (InputStream in = new FileInputStream(tmp.toFile());
             OutputStream out = new FileOutputStream(dest.toFile())) {
            long copied = in.transferTo(out);
            System.out.println("transferTo copied " + copied + " bytes");
        }

        section("6) ByteArrayOutputStream — in-memory output");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 100; i++) baos.write(i);
        byte[] inMem = baos.toByteArray();
        System.out.println("in-memory size = " + inMem.length);

        section("7) ByteArrayInputStream — read from a byte[]");
        try (InputStream in = new ByteArrayInputStream(inMem)) {
            byte[] sample = new byte[5];
            int n = in.read(sample);
            System.out.println("read " + n + " bytes from memory: "
                    + sample[0] + " " + sample[1] + " " + sample[2] + " " + sample[3] + " " + sample[4]);
        }

        Files.deleteIfExists(tmp);
        Files.deleteIfExists(dest);
        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
