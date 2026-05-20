package Phase8_PracticalAPIs.FileIO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Data Streams — DataInputStream / DataOutputStream
 * --------------------------------------------------
 * DECORATORS on top of byte streams that read and write Java's PRIMITIVE
 * TYPES in a portable, fixed-width binary format:
 *
 *      int (4 bytes), long (8), short (2), byte (1),
 *      float (4), double (8), boolean (1), char (2),
 *      "modified UTF-8" strings via writeUTF / readUTF.
 *
 * Byte order is BIG-ENDIAN, regardless of platform.
 *
 *
 * Why this exists
 * ---------------
 * Without DataStreams you'd have to encode/decode each primitive by
 * hand (shifts, masks, byte arrays). DataStreams give you the same
 * format any other Java program will agree on.
 *
 *
 * Pattern
 * -------
 *      try (var out = new DataOutputStream(new FileOutputStream(f))) {
 *          out.writeInt(42);
 *          out.writeUTF("hello");
 *      }
 *      try (var in = new DataInputStream(new FileInputStream(f))) {
 *          int n = in.readInt();
 *          String s = in.readUTF();
 *      }
 *
 *
 * Caveats
 * -------
 *   1. readUTF / writeUTF use Java's MODIFIED UTF-8 (not standard UTF-8).
 *      For interop with other languages, use a Reader with UTF_8 or a
 *      proper protocol library.
 *   2. The format is positional. There's no schema, no field names,
 *      and no versioning. For real data, prefer JSON / Protobuf /
 *      Avro / Java serialization.
 *   3. End-of-stream — methods like readInt throw EOFException (not -1).
 */

public class DataStreams {

    public static void main(String[] args) throws IOException {

        Path tmp = Files.createTempFile("data-demo-", ".bin");

        section("1) Write a few primitives + a UTF string");
        try (OutputStream raw = new FileOutputStream(tmp.toFile());
             DataOutputStream out = new DataOutputStream(raw)) {
            out.writeInt(42);
            out.writeLong(1234567890123L);
            out.writeDouble(3.1415926535);
            out.writeBoolean(true);
            out.writeUTF("hello, data");
        }
        System.out.println("file size = " + Files.size(tmp) + " bytes");

        section("2) Read them back in the SAME ORDER");
        try (InputStream raw = new FileInputStream(tmp.toFile());
             DataInputStream in = new DataInputStream(raw)) {
            int    a = in.readInt();
            long   b = in.readLong();
            double c = in.readDouble();
            boolean d = in.readBoolean();
            String s = in.readUTF();
            System.out.println("int=" + a + ", long=" + b + ", double=" + c
                    + ", bool=" + d + ", text='" + s + "'");
        }

        section("3) Reading past EOF throws EOFException");
        try (DataInputStream in = new DataInputStream(new FileInputStream(tmp.toFile()))) {
            try {
                while (true) in.readInt();
            } catch (EOFException eof) {
                System.out.println("expected: EOFException after reading the bytes");
            }
        }

        section("4) Loop-write + loop-read using a sentinel");
        Path tmp2 = Files.createTempFile("data-loop-", ".bin");
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(tmp2.toFile()))) {
            for (int i = 1; i <= 5; i++) out.writeInt(i * i);
        }
        try (DataInputStream in = new DataInputStream(new FileInputStream(tmp2.toFile()))) {
            try {
                while (true) System.out.println("  square = " + in.readInt());
            } catch (EOFException ignored) { /* done */ }
        }

        Files.deleteIfExists(tmp);
        Files.deleteIfExists(tmp2);
        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
