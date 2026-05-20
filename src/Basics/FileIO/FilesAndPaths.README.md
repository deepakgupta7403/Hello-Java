# `java.nio.file` — `Path` and `Files`

The modern Java file API (since 7). Two main types:

| Type | What it is |
|---|---|
| `Path` | An immutable file-system path. Replaces `java.io.File`. |
| `Files` | A class of **static utility methods** that do all the work — read, write, copy, move, walk, attributes, streams, … |

## Constructing Paths
```java
Path p1 = Path.of("src", "Basics", "FileIO", "FilesAndPaths.java");
Path p2 = Paths.get("/var/log/syslog");           // pre-Java-11 equivalent
Path p3 = file.toPath();                          // from a java.io.File
```

## Useful `Path` methods
| Method | Purpose |
|---|---|
| `getFileName()` | Leaf name |
| `getParent()` | Directory |
| `getRoot()` | Root (drive on Windows) |
| `resolve("child")` | Append a child path |
| `relativize(other)` | Path from this to `other` |
| `normalize()` | Collapse `.` and `..` |
| `toAbsolutePath()` | Make absolute |
| `toRealPath()` | …and resolve symlinks |
| `startsWith` / `endsWith` | Structural matching, not string-matching |

## Useful `Files` methods
| Method | Purpose |
|---|---|
| `createFile` / `createDirectory(/ies)` | Create |
| `createTempFile` / `createTempDirectory` | Create with a unique name |
| `exists` / `isRegularFile` / `isDirectory` / `isReadable` / `isWritable` | Probe |
| `size(path)` | Length in bytes |
| `readAllBytes` / `readString` / `readAllLines` | Read wholesale |
| `write` / `writeString` | Write wholesale |
| `copy` / `move` / `delete[IfExists]` | Mutate |
| `readAttributes` / `setLastModifiedTime` / `setPosixFilePermissions` | Metadata |
| `list` / `walk` / `lines` | Return `Stream` — see `FilesWalkAndList.java` |
| `newBufferedReader` / `newBufferedWriter` | Charset-aware buffered |

## Common options
| Constant | Meaning |
|---|---|
| `StandardOpenOption.APPEND` | Append instead of overwrite |
| `StandardOpenOption.CREATE` / `CREATE_NEW` | Create / fail if exists |
| `StandardOpenOption.TRUNCATE_EXISTING` | Truncate on open |
| `StandardCopyOption.REPLACE_EXISTING` | Overwrite the target |
| `StandardCopyOption.ATOMIC_MOVE` | Atomic move (same FS) |
| `StandardCopyOption.COPY_ATTRIBUTES` | Preserve metadata |

## Why prefer `Files` over `java.io.File`
- Better error messages (`NoSuchFileException`, `AccessDeniedException`).
- Symlinks first-class.
- Atomic copy / move / delete.
- Streams (`Files.lines`, `Files.list`, `Files.walk`).
- Watch services for change notification.
- Pluggable file systems (a ZIP file is a virtual FS).

## Run
```bash
cd src
java Basics.FileIO.FilesAndPaths
```

## See also
- `FilesWalkAndList.java` — directory streams.
- `BufferedStreams.java` — `Files.newBufferedReader`.
- `ModernFileIO.java` — Java 11+ shortcuts.
- `WatchServiceDemo.java` — observe changes.
