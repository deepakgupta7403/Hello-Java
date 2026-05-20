# File I/O — Introduction

Java has two file APIs you should know:

| API | Since | Today |
|---|---|---|
| `java.io` | 1.0 | Classic stream-based I/O. Byte streams + character streams + decorators. Still everywhere in legacy code. |
| `java.nio.file` | 7 | Modern `Path` + `Files` utility methods. **Preferred for new code.** |

They interoperate: `path.toFile()` and `file.toPath()`.

## The stream hierarchy
```
InputStream / OutputStream  — bytes (8-bit)
Reader / Writer             — characters (Unicode text)
Buffered*                   — buffering decorator
Data*                       — primitive read/write decorator
Object*                     — serialization decorator
```

## Quick "which class for what?"
| Need | Use |
|---|---|
| Read text wholesale | `Files.readString(path)` (Java 11+) |
| Write text wholesale | `Files.writeString(path, text)` |
| Read line by line | `Files.lines(path)` or `Files.newBufferedReader` |
| Read binary | `Files.readAllBytes(path)` |
| Random access | `FileChannel` / `RandomAccessFile` |
| List a directory | `Files.list(path)` / `Files.walk(path)` |
| Watch for changes | `WatchService` |
| Serialise an object | `ObjectOutputStream` / `ObjectInputStream` |

## Resource hygiene
Anything that opens a file goes in `try`-with-resources. The JVM does **not**
close streams on GC promptly — leaks are common.
```java
try (var reader = Files.newBufferedReader(path, UTF_8)) {
    // ...
}
```

## Character encodings
**Always specify the charset** for text I/O. The platform default differs by
OS (Windows often defaults to a non-UTF-8 charset). Use:
```java
import static java.nio.charset.StandardCharsets.UTF_8;
```

## Run
```bash
cd src
java Basics.FileIO.FileIOIntroduction
```

## See also
- `ByteStreams.java` / `CharacterStreams.java` — the building blocks.
- `BufferedStreams.java` — why buffering matters.
- `FilesAndPaths.java` — modern `java.nio.file`.
- `ModernFileIO.java` — Java 11+ conveniences.
