# `WatchService` — observe file-system changes

The OS knows when files change; the JVM exposes that through a `WatchService`.
You **register** directories for events of interest, then **poll** for changes.

## Event kinds
| Kind | When |
|---|---|
| `ENTRY_CREATE` | A new file / directory appeared |
| `ENTRY_MODIFY` | Contents changed |
| `ENTRY_DELETE` | Removed |
| `OVERFLOW` | Events were lost (buffer overflowed) — always handle this |

## Skeleton
```java
WatchService ws = FileSystems.getDefault().newWatchService();
dir.register(ws,
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_DELETE);

while (running) {
    WatchKey key = ws.take();                     // or poll(timeout, unit)
    for (WatchEvent<?> ev : key.pollEvents()) {
        Object ctx = ev.context();                // usually a relative Path
        handle(ev.kind(), ctx);
    }
    if (!key.reset()) break;                      // key invalid → directory gone
}
ws.close();
```

## Caveats
- **Watches directories, not files.** Events are reported with file names relative to the directory.
- **Not recursive.** Walk the tree and register each subdirectory yourself.
- **Platform-dependent.** macOS implementation uses polling (events can be a beat late). Windows and Linux use kernel notifications.
- **One write can produce two MODIFY events.** Debounce in user code.
- **OVERFLOW is real.** Under burst load, you must re-scan the directory to recover.
- **Doesn't keep the JVM alive on its own** — pair with a daemon thread or block the main thread on `take()`.

## Recursive watcher pattern
```java
Files.walk(root).filter(Files::isDirectory).forEach(d -> register(d, ws));
// then, in your loop, when ENTRY_CREATE fires on a Path that is a directory:
register(newDir, ws);
```

## Alternatives
- **Polling with `Files.getLastModifiedTime`** — simpler, cross-platform, but you choose the polling rate.
- **Apache Commons IO `FileAlterationMonitor`** — recursive, debounced wrapper.
- **Reactive libraries** (Spring's `WatchEventPublisher`, Reactor's `DirectoryWatcher`) — higher-level streams.

## Use cases
| Use | Note |
|---|---|
| Hot-reload of config files | Debounce + re-read inside the event handler |
| Auto-rebuilds in tooling | What a file-watcher CLI does |
| Sync / replication | One side watches, the other receives |
| Audit / security | Combined with `getOwner()` and timestamps |

## Run
```bash
cd src
java Basics.FileIO.WatchServiceDemo
```

## See also
- `FilesAndPaths.java`, `FilesWalkAndList.java`.
- `Multithreading/Threads.java` — usually pair the watcher with a daemon thread.
