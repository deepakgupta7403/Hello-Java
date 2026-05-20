# Buffered Streams

Reading or writing one byte (or char) at a time hits the OS for every call.
A buffer slurps a big chunk at once and serves it from memory — cheap and
10–50× faster for byte-by-byte loops.

## The four classes
| Wraps | Buffered wrapper |
|---|---|
| `InputStream` | `BufferedInputStream` |
| `OutputStream` | `BufferedOutputStream` |
| `Reader` | `BufferedReader` |
| `Writer` | `BufferedWriter` |

## Pattern
```java
try (var in = new BufferedInputStream(new FileInputStream(file))) {
    // byte-by-byte reads are now cheap
}

try (var r = Files.newBufferedReader(path, UTF_8)) {
    String line;
    while ((line = r.readLine()) != null) process(line);
}
```

## Useful extras
| Method | Purpose |
|---|---|
| `BufferedReader.readLine()` | Read up to next `\n` / `\r\n` |
| `BufferedReader.lines()` | `Stream<String>` of lines (Java 8+) |
| `BufferedWriter.newLine()` | Platform-correct line separator |

## When you don't need to wrap
- You're already reading in big chunks (`read(buf)` with ≥8 KB).
- You're using `Files.readAllBytes` / `readString` — those buffer internally.
- File is tiny.

## `flush` vs `close`
`close()` flushes for you. Call `flush()` explicitly only if downstream
consumers should see partial output before close (logging, sockets).

## Performance rule of thumb
| Pattern | Speed |
|---|---|
| `for-each` byte through `FileInputStream.read()` | 🐌 |
| Same wrapped in `BufferedInputStream` | 🚀 |
| `read(byte[] buf)` with a sensible buf | 🚀 |
| `Files.readAllBytes(path)` | 🚀 |

## Run
```bash
cd src
java Basics.FileIO.BufferedStreams
```

The micro-benchmark inside should show the buffered version finishing in a
fraction of the time.

## See also
- `ByteStreams.java`, `CharacterStreams.java` — the unbuffered cousins.
- `ModernFileIO.java` — Java 11+ one-liners.
- `FilesAndPaths.java` — `Files.newBufferedReader` shortcut.
