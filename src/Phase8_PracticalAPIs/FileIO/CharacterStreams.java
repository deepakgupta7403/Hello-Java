package Phase8_PracticalAPIs.FileIO;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Character Streams — Reader / Writer
 * -----------------------------------
 * Java 1.1 added Reader / Writer to address the fundamental mismatch
 * between bytes and characters in modern encodings (UTF-8, UTF-16,
 * GB-18030, ...). A single character can be 1-4 bytes, so reading
 * "text" through an InputStream is wrong.
 *
 *
 * Reader — read characters
 * ------------------------
 *      int read()                  -> next char 0..65535, -1 at EOF
 *      int read(char[] cbuf)
 *      int read(char[] cbuf, off, len)
 *      void close()
 *
 *
 * Writer — write characters
 * -------------------------
 *      void write(int c)
 *      void write(char[] cbuf)
 *      void write(String s)
 *      void write(String s, off, len)
 *      void append(char c)         -> chainable
 *      void flush()
 *
 *
 * The bridge classes
 * ------------------
 *      InputStreamReader(InputStream, Charset)   - bytes -> chars
 *      OutputStreamWriter(OutputStream, Charset) - chars -> bytes
 *
 * Use these to wrap a byte stream when you need text from a non-File
 * source (a Socket, stdin, an HTTP response).
 *
 *
 * Concrete impls
 * --------------
 *      FileReader / FileWriter      - file-flavoured (use the charset-aware
 *                                     constructors, NOT the default ones).
 *      StringReader / StringWriter - in-memory.
 *      CharArrayReader/Writer       - in-memory, char[].
 *      PrintWriter                  - convenience formatting layer.
 *
 *
 * Charset trap
 * ------------
 * `new FileReader(file)` uses the platform default charset. That's a
 * portability landmine. Prefer the Java 11+ charset-aware constructors
 * or use Files.newBufferedReader(path, UTF_8).
 */

public class CharacterStreams {

    public static void main(String[] args) throws IOException {

        Path tmp = Files.createTempFile("char-demo-", ".txt");

        section("1) FileWriter + UTF-8 — write text");
        try (Writer out = new FileWriter(tmp.toFile(), StandardCharsets.UTF_8)) {
            out.write("hello, file\n");
            out.write("Java 21 ❤️ (heart emoji)\n");
            out.append("appended line\n");
        }
        System.out.println("file size = " + Files.size(tmp) + " bytes");

        section("2) FileReader + UTF-8 — read char by char");
        try (Reader in = new FileReader(tmp.toFile(), StandardCharsets.UTF_8)) {
            int c;
            while ((c = in.read()) != -1) System.out.print((char) c);
        }

        section("3) FileReader + buffer (fast)");
        char[] buf = new char[256];
        try (Reader in = new FileReader(tmp.toFile(), StandardCharsets.UTF_8)) {
            int n = in.read(buf);
            System.out.println("read " + n + " chars; first 5 = '"
                    + new String(buf, 0, Math.min(5, n)) + "'");
        }

        section("4) The byte-to-char bridge: InputStreamReader");
        // Reading text from a NON-file InputStream (here, a byte array
        // that we pretend came from a network socket).
        byte[] body = "fetched from net".getBytes(StandardCharsets.UTF_8);
        try (Reader r = new InputStreamReader(new java.io.ByteArrayInputStream(body), StandardCharsets.UTF_8)) {
            int n = r.read(buf);
            System.out.println("decoded text = '" + new String(buf, 0, n) + "'");
        }

        section("5) The char-to-byte bridge: OutputStreamWriter");
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        try (Writer w = new OutputStreamWriter(bytes, StandardCharsets.UTF_8)) {
            w.write("café ☕");
        }
        System.out.println("encoded UTF-8 bytes = " + bytes.size());

        section("6) StringReader / StringWriter — in-memory");
        StringWriter sw = new StringWriter();
        sw.write("hello, ");
        sw.write("world");
        sw.append('!');
        System.out.println("StringWriter holds: " + sw);

        try (StringReader sr = new StringReader("xyz abc")) {
            int n = sr.read(buf);
            System.out.println("read from StringReader: '" + new String(buf, 0, n) + "'");
        }

        Files.deleteIfExists(tmp);
        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
