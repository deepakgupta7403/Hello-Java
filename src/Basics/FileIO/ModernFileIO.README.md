# Modern File I/O — Java 11 → 21

The text-handling shortcuts most of your code should reach for first.

## Java 11
| API | Purpose |
|---|---|
| `Files.readString(path[, charset])` | Slurp text |
| `Files.writeString(path, text[, charset][, options])` | Write text |
| `String.lines()` | `Stream<String>` over the string's lines |
| `String.repeat(n)` | "abc".repeat(3) → "abcabcabc" |
| `String.isBlank()` / `String.strip()` / `stripLeading()` / `stripTrailing()` | Unicode-aware blank handling |

## Java 12+
- `String.indent(n)` — add/remove leading whitespace.
- `String.transform(Function<String, R>)` — apply a function fluently.
- `Files.mismatch(a, b)` — first differing byte, or `-1` if equal.

## Java 13+
- `String.formatted(args)` — fluent `String.format`.
- **Text blocks** — `"""..."""` raw multi-line strings.

## Java 16+
- `Stream.toList()` — convenient and unmodifiable; preferred over `Collectors.toList()`.

## Java 17+
- Sealed type hierarchies + pattern matching make reading "different shapes of records" cleaner.

## Cheat sheet
| Task | Code |
|---|---|
| Read file as one string | `Files.readString(path)` |
| Read file as `List<String>` | `Files.readAllLines(path)` |
| Stream lines lazily | `Files.lines(path)` — close it! |
| Write text | `Files.writeString(path, s)` |
| Append text | `Files.writeString(path, s, APPEND)` |
| Write list of lines | `Files.write(path, list)` |
| Compare two files | `Files.mismatch(a, b)` |
| Pipe a stream to file | `Files.copy(inputStream, path)` |

## Always specify the charset
```java
Files.writeString(path, text, StandardCharsets.UTF_8);
Files.readString(path, StandardCharsets.UTF_8);
```
Otherwise you'll pick up the platform default and ship a Windows-vs-everything-else bug.

## When to fall back to old APIs
| Need | Pick |
|---|---|
| Stream a huge file | `Files.lines` / `BufferedReader.lines` (don't read it all into memory) |
| Random access | `FileChannel` / `RandomAccessFile` |
| Binary protocol | `DataInputStream` / `DataOutputStream` |
| Watching for changes | `WatchService` |

## Run
```bash
cd src
java Basics.FileIO.ModernFileIO
```

## See also
- `FilesAndPaths.java` — `Path` + `Files` core.
- `BufferedStreams.java` — when `readString` is too eager.
- `FileChannelDemo.java` — when streams aren't enough.
