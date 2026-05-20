package Phase9_ModernJavaAndModules.Modules;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * ServiceLoader — the Built-In Plugin / SPI Mechanism
 * ---------------------------------------------------
 * ServiceLoader lets you declare a SERVICE INTERFACE and DISCOVER
 * implementations at runtime — without hard-coding their names.
 *
 *
 * Two pieces
 * ----------
 *   1. A service INTERFACE (or abstract class).
 *   2. ONE or more implementations, declared so the JVM can find them.
 *
 *
 * Declaring providers
 * -------------------
 *
 *   Classpath / unnamed module: a META-INF/services/<interface name>
 *   text file listing implementation classes, one per line:
 *
 *       META-INF/services/com.example.api.Codec
 *       -----------------------------------------
 *       com.example.codec.GzipCodec
 *       com.example.codec.SnappyCodec
 *
 *   Modular projects: declare in module-info.java instead:
 *
 *       module com.example.codec {
 *           requires com.example.api;
 *           provides com.example.api.Codec
 *               with com.example.codec.GzipCodec,
 *                    com.example.codec.SnappyCodec;
 *       }
 *
 *
 * Loading at runtime
 * ------------------
 *      ServiceLoader<Codec> loader = ServiceLoader.load(Codec.class);
 *      for (Codec c : loader) { ... }
 *
 *      loader.stream()
 *            .map(ServiceLoader.Provider::get)
 *            .filter(c -> c.canHandle(input))
 *            .findFirst();
 *
 *
 * Real-world examples
 * -------------------
 *   - JDBC drivers (java.sql.Driver) — historic, before SPI
 *     formalisation but now uses it.
 *   - Logging (SLF4J, java.util.spi providers).
 *   - Java Sound, Cryptography Providers, Locale providers, Charsets.
 *
 *
 * Demo — this file
 * ----------------
 * Without a module-info.java OR a META-INF/services entry, our
 * demo Codec interface has zero registered providers. The file walks
 * through the API and lists JDK-provided SPIs that DO have providers
 * (so we can show ServiceLoader actually finding things).
 */

public class ServiceLoaderDemo {

    /** A toy SPI for the demo. */
    public interface Codec {
        String name();
        byte[] encode(byte[] in);
    }

    public static void main(String[] args) {

        section("1) Our toy SPI has zero providers (nothing registered)");
        ServiceLoader<Codec> codecs = ServiceLoader.load(Codec.class);
        Iterator<Codec> it = codecs.iterator();
        if (!it.hasNext()) System.out.println("  no Codec providers found");
        else codecs.forEach(c -> System.out.println("  " + c.name()));

        section("2) JDK-provided SPI: java.sql.Driver (if drivers are on classpath)");
        listProviders(java.sql.Driver.class);

        section("3) JDK-provided SPI: java.nio.file.spi.FileSystemProvider");
        listProviders(java.nio.file.spi.FileSystemProvider.class);

        section("4) JDK-provided SPI: java.nio.charset.spi.CharsetProvider");
        listProviders(java.nio.charset.spi.CharsetProvider.class);

        section("5) Stream API — pick a provider lazily");
        codecs.stream()
              .map(ServiceLoader.Provider::type)
              .forEach(c -> System.out.println("  candidate: " + c.getName()));

        section("done — wire a real META-INF/services file to see providers appear");
    }

    private static <T> void listProviders(Class<T> spi) {
        ServiceLoader<T> sl = ServiceLoader.load(spi);
        int count = 0;
        for (T provider : sl) {
            System.out.println("  " + provider.getClass().getName());
            count++;
        }
        if (count == 0) System.out.println("  (no providers registered for " + spi.getSimpleName() + ")");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
