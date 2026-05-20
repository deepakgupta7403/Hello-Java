package Basics.FileIO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Object Streams — ObjectOutputStream / ObjectInputStream
 * -------------------------------------------------------
 * Read and write WHOLE OBJECT GRAPHS with one call each. The class must
 * implement Serializable; the stream then records every reachable field.
 * Used by RMI historically, by Java's built-in clone-via-serialization,
 * and occasionally for cache files.
 *
 *
 * Methods you actually use
 * ------------------------
 *      out.writeObject(obj)
 *      Object o = in.readObject();           // throws ClassNotFoundException
 *      in.close() / out.close()
 *
 *
 * Serializable contract
 * ---------------------
 *   - Mark the class with `implements Serializable`.
 *   - Declare a serialVersionUID. Annotate with @Serial (Java 14+).
 *   - Non-serializable fields must be `transient`.
 *   - super-class chain must be Serializable, or must have a no-arg
 *     constructor reachable for deserialization.
 *
 *
 * DON'T USE THIS FOR REAL EXTERNAL DATA
 * -------------------------------------
 * Java serialization is famous for:
 *   - SECURITY HOLES — deserialising untrusted bytes can run code
 *     (gadget chains).
 *   - VERSIONING FRAGILITY — small field changes break the format.
 *   - VERBOSITY and SLOWNESS vs JSON/Protobuf.
 *
 * Use this API only for trusted, internal, short-lived data. For
 * config, APIs, cross-language: use JSON / Protobuf / Avro.
 *
 *
 * Java 17+
 * --------
 * The "serialization filter" (jdk.serialFilter / ObjectInputFilter)
 * lets you allow-list classes that can be deserialised — partial
 * mitigation for gadget-chain risk.
 */

public class ObjectStreams {

    record Book(String title, String author, int pages) implements Serializable {
        @Serial private static final long serialVersionUID = 1L;
    }

    /** A graph of books to serialise. */
    record Library(String name, List<Book> books, Book featured) implements Serializable {
        @Serial private static final long serialVersionUID = 1L;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Path tmp = Files.createTempFile("obj-demo-", ".ser");

        section("1) writeObject — serialize a graph");
        Book a = new Book("Effective Java", "Bloch", 416);
        Book b = new Book("Java Concurrency in Practice", "Goetz", 384);
        Library lib = new Library("Bookshelf", List.of(a, b), a);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmp.toFile()))) {
            out.writeObject(lib);
        }
        System.out.println("wrote " + Files.size(tmp) + " bytes");

        section("2) readObject — deserialize back");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmp.toFile()))) {
            Library back = (Library) in.readObject();
            System.out.println("library = " + back.name());
            for (Book bk : back.books()) System.out.println("  " + bk);
            System.out.println("featured = " + back.featured());
            System.out.println("same-instance reference preserved? "
                    + (back.books().get(0) == back.featured()));
            // The book at index 0 and the `featured` field referenced the
            // same instance. Object streams preserve that.
        }

        section("3) ClassNotFoundException — class missing on read side");
        // Skipped: would require a separate classloader to simulate.
        // The exception is thrown when the writer's class can't be
        // resolved on the reader's classpath.

        section("4) transient — skip a field");
        // class WithSecret implements Serializable {
        //     String user;
        //     transient String password;       // <-- not written
        // }

        section("5) Modern alternatives");
        System.out.println("  - For JSON: Jackson / Gson / built-in JEP 8 stream");
        System.out.println("  - For protocols: Protobuf, Avro, FlatBuffers");
        System.out.println("  - For caching: Redis with a chosen format");

        Files.deleteIfExists(tmp);
        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
