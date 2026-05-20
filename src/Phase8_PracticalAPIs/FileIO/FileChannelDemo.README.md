# `FileChannel` — Random Access, Memory Mapping, Locks

The NIO answer to `RandomAccessFile` plus several OS-level features:
- **Random access** via `position()`.
- **Bulk transfer** through `ByteBuffer`.
- **Channel-to-channel copy** (`transferTo` / `transferFrom`) — zero-copy on most OSes.
- **Memory-mapped files** (`MappedByteBuffer`).
- **OS-level file locks** (advisory).

## How to obtain one
```java
FileChannel ch = FileChannel.open(path,
        StandardOpenOption.READ, StandardOpenOption.WRITE);
// or, legacy:
FileChannel ch = new RandomAccessFile(file, "rw").getChannel();
```

## Mental model
- The channel has a **position** — where the next read/write happens.
- You move bytes through a `ByteBuffer`.
- `ByteBuffer` has its own `position` / `limit` / `capacity`. Call `flip()` between filling and draining.

```java
ByteBuffer buf = ByteBuffer.allocate(1024);
int n = ch.read(buf);     // fill
buf.flip();               // prepare for reading
byte[] dst = new byte[n];
buf.get(dst);             // drain
```

## Memory-mapped files
```java
MappedByteBuffer m = ch.map(MapMode.READ_ONLY, 0, ch.size());
```
Lightning-fast reads — OS handles paging. Caveats:
- The mapping isn't released until GC; explicit cleanup is JVM-specific.
- On Windows, the file may stay locked until the JVM exits.
- Mapping huge files in a loop will exhaust virtual address space (on 32-bit JVMs especially).

## File locks (`FileLock`)
```java
try (var lock = ch.lock()) {     // exclusive
    // critical section across processes
}
```
- Advisory only — other processes that don't check the lock can still touch the file.
- Use `tryLock()` for non-blocking acquisition.
- Lock-on-region (offset + length) is supported.

## When to reach for FileChannel
| Need | Use |
|---|---|
| Random-access update of a large file | `FileChannel.position()` |
| Index file with binary search | Map the file, do `getLong(offset)` |
| High-throughput copy | `transferTo` |
| Cross-process file locking | `FileChannel.lock()` |
| Just read text | `Files.readString` — don't reach for channels |

## `RandomAccessFile` vs `FileChannel`
| | `RandomAccessFile` | `FileChannel` |
|---|---|---|
| Position seek | Yes | Yes |
| ByteBuffer | No | Yes |
| `transferTo` zero-copy | No | Yes |
| Memory mapping | No (use channel) | Yes |
| Era | Java 1.0 | Java 1.4 |

Prefer `FileChannel` for new code.

## Run
```bash
cd src
java Basics.FileIO.FileChannelDemo
```

## See also
- `FilesAndPaths.java`, `BufferedStreams.java`.
- `WatchServiceDemo.java`.
