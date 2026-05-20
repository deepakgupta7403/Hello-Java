# Character Streams — `Reader` / `Writer`

Java 1.1 added `Reader` / `Writer` to handle modern encodings (UTF-8,
UTF-16, GB-18030, …) where one character may be 1–4 bytes. Reading "text"
through a byte stream is wrong.

## Core methods
| Reader | Writer |
|---|---|
| `int read()` — next char or -1 | `void write(int c)` |
| `int read(char[] buf)` | `void write(char[] buf)` |
| `int read(char[] buf, off, len)` | `void write(String s)` |
| `void close()` | `void flush()` / `void close()` |

## Bridge classes (byte ↔ char)
```java
new InputStreamReader (inputStream,  StandardCharsets.UTF_8);   // bytes -> chars
new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);   // chars -> bytes
```
Use these to read/write text from non-file sources (sockets, stdin, HTTP).

## Concrete implementations
| Class | Purpose |
|---|---|
| `FileReader(file, charset)` | Read text from a file (use the Java 11+ charset constructor) |
| `FileWriter(file, charset[, append])` | Write text to a file |
| `StringReader(text)` | In-memory char source |
| `StringWriter` | In-memory char sink |
| `CharArrayReader` / `Writer` | Same idea, backed by `char[]` |
| `PrintWriter` | Adds `println`, `printf` formatting |

## ⚠️ Charset trap
**`new FileReader(file)`** (no charset) uses the *platform default*. On
Windows this is rarely UTF-8 — your code will read fine on macOS/Linux and
produce gibberish on Windows. Always specify:
```java
new FileReader(file, StandardCharsets.UTF_8)
new FileWriter(file, StandardCharsets.UTF_8)
```

Better: `Files.newBufferedReader(path, UTF_8)` — buffered, charset-aware,
and you don't even need a `File` object.

## When to use Reader/Writer vs byte streams
| Data | Use |
|---|---|
| Text (logs, JSON, CSV, source code) | Reader / Writer + charset |
| Binary (image, PDF, ZIP) | Byte streams |
| Mixed (network protocol) | Byte streams as transport; `InputStreamReader` for text frames |

## Run
```bash
cd src
java Basics.FileIO.CharacterStreams
```

## See also
- `ByteStreams.java` — the byte-level cousin.
- `BufferedStreams.java` — wrap for performance.
- `ModernFileIO.java` — `Files.readString` / `writeString` shortcuts.
