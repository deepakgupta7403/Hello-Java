# Byte Streams — `InputStream` / `OutputStream`

The grandparents of Java I/O. They read and write **raw bytes**. Used for
non-textual data (images, audio, archives) or as the lower layer beneath
character streams.

## Core methods
| Method | Class | What it does |
|---|---|---|
| `int read()` | `InputStream` | Next byte 0..255, or -1 at EOF |
| `int read(byte[] b)` | `InputStream` | Fill the array; returns bytes read |
| `byte[] readAllBytes()` | `InputStream` (Java 9+) | Slurp the rest of the stream |
| `long transferTo(OutputStream)` | `InputStream` (Java 9+) | Copy stream-to-stream |
| `void write(int b)` | `OutputStream` | Write low 8 bits |
| `void write(byte[] b)` | `OutputStream` | Write entire array |
| `void flush()` | `OutputStream` | Force buffered output through |
| `void close()` | Both | Release the file handle |

## Concrete file impls
| Class | Purpose |
|---|---|
| `FileInputStream` | Read bytes from a file |
| `FileOutputStream` | Write bytes (optional append mode) |
| `ByteArrayInputStream` | Read from a `byte[]` (in-memory) |
| `ByteArrayOutputStream` | Write to an in-memory `byte[]` |

## Best practices
1. **Always try-with-resources.**
2. **Don't read one byte at a time** — use `read(byte[] buf)` with ~8 KiB, or wrap with `BufferedInputStream`.
3. For text, use `Reader`/`Writer`, **not** `InputStream` + `new String(bytes)`.
4. `flush()` before close if downstream depends on partial output (logging, sockets).

## Modern shortcuts
```java
byte[] all = Files.readAllBytes(path);
Files.write(path, bytes);                          // overwrite
Files.write(path, bytes, StandardOpenOption.APPEND);
in.transferTo(out);                                // stream-to-stream
```

## When to use byte streams
| Need | Use byte stream? |
|---|---|
| Image, PDF, ZIP | ✅ Byte stream |
| Plain text | ❌ Use `Reader` / `Writer` |
| Random access | Use `FileChannel` or `RandomAccessFile` |
| Just give me the bytes | `Files.readAllBytes` |

## Run
```bash
cd src
java Basics.FileIO.ByteStreams
```

## See also
- `CharacterStreams.java` — text I/O.
- `BufferedStreams.java` — why buffering matters.
- `FilesAndPaths.java` — `Files` utilities.
