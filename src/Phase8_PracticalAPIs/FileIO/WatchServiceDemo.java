package Phase8_PracticalAPIs.FileIO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/**
 * WatchService — observe file-system changes
 * ------------------------------------------
 * The OS knows when files change; the JVM exposes that through a
 * WatchService. You REGISTER directories with the service for events
 * of interest, then POLL for changes.
 * <p>
 *
 * Event kinds
 * -----------
 *   ENTRY_CREATE   - a file/dir was created
 *   ENTRY_MODIFY   - contents changed
 *   ENTRY_DELETE   - deleted
 *   OVERFLOW       - events were lost (buffer ran out)
 * <p>
 *
 * Lifecycle
 * ---------
 *      WatchService ws = FileSystems.getDefault().newWatchService();
 *      WatchKey key = dir.register(ws,
 *              StandardWatchEventKinds.ENTRY_CREATE,
 *              StandardWatchEventKinds.ENTRY_MODIFY);
 * <p>
 *
 *      while (true) {
 *          WatchKey k = ws.take();           // or poll(timeout, unit)
 *          for (WatchEvent<?> ev : k.pollEvents()) { ... }
 *          if (!k.reset()) break;            // key invalid -> dir gone
 *      }
 * <p>
 *
 * Caveats
 * -------
 *   - Watch service watches DIRECTORIES, not individual files (events
 *     are reported with file names relative to the directory).
 *   - It is NOT recursive — you must register each subdirectory.
 *   - Implementation varies by OS. On macOS it's polling-based and may
 *     report MODIFY twice in quick succession.
 *   - One ENTRY_MODIFY may correspond to several writes (debounce).
 *   - It DOES NOT block the JVM from exiting.
 * <p>
 *
 * This demo writes a few files in a separate thread and polls for the
 * events for a couple of seconds.
 */

public class WatchServiceDemo {

    public static void main(String[] args) throws IOException, InterruptedException {

        Path dir = Files.createTempDirectory("watch-demo-");
        System.out.println("watching " + dir);

        WatchService ws = FileSystems.getDefault().newWatchService();
        dir.register(ws,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

        // background "writer" — creates and modifies files
        Thread writer = new Thread(() -> {
            try {
                Path f = dir.resolve("hello.txt");
                Files.writeString(f, "first\n", StandardCharsets.UTF_8);
                Thread.sleep(120);
                Files.writeString(f, "second\n", StandardCharsets.UTF_8,
                        java.nio.file.StandardOpenOption.APPEND);
                Thread.sleep(120);
                Files.delete(f);
            } catch (Exception ignored) {}
        }, "writer");
        writer.start();

        // Watcher loop — drain events for ~1.5 seconds.
        long deadline = System.currentTimeMillis() + 1500;
        while (System.currentTimeMillis() < deadline) {
            WatchKey key = ws.poll(200, TimeUnit.MILLISECONDS);
            if (key == null) continue;
            for (WatchEvent<?> ev : key.pollEvents()) {
                Object ctx = ev.context();              // typically a relative Path
                System.out.println("  " + ev.kind().name() + " -> " + ctx);
            }
            if (!key.reset()) { System.out.println("key no longer valid"); break; }
        }

        writer.join();
        ws.close();
        // Cleanup directory
        try (var s = Files.walk(dir)) {
            s.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.delete(p); } catch (IOException ignored) {}
            });
        }
        System.out.println("done");
    }
}
