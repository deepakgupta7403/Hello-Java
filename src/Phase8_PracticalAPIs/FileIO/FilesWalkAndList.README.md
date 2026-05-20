# Walking the file system

`Files.list` / `walk` / `find` / `lines` return `Stream<...>` so you can
pipe through filter, map, and collect.

| Method | What you get |
|---|---|
| `Files.list(dir)` | `Stream<Path>` — direct children only |
| `Files.walk(dir [, maxDepth])` | `Stream<Path>` — dir and descendants |
| `Files.find(dir, depth, BiPredicate)` | Walk + filter on `(Path, BasicFileAttributes)` |
| `Files.lines(path [, charset])` | `Stream<String>` of lines |
| `Files.walkFileTree(dir, FileVisitor)` | Lowest-level visitor with pre/post/visit/fail hooks |

## ⚠️ Always close the stream
Every one of these holds an OS file handle. **Wrap in try-with-resources**:
```java
try (Stream<Path> s = Files.walk(root)) {
    s.forEach(...);
}
```

## Idioms

**Find `.java` files recursively:**
```java
try (Stream<Path> s = Files.walk(root)) {
    List<Path> javas = s.filter(p -> p.toString().endsWith(".java")).toList();
}
```

**Line-count a tree:**
```java
try (Stream<Path> s = Files.walk(root)) {
    long lines = s.filter(Files::isRegularFile)
                  .flatMap(p -> {
                      try { return Files.lines(p); }
                      catch (IOException e) { throw new UncheckedIOException(e); }
                  }).count();
}
```

**Recursive delete (no built-in helper):**
```java
try (Stream<Path> s = Files.walk(dir)) {
    s.sorted(Comparator.reverseOrder())
     .forEach(p -> { try { Files.delete(p); } catch (IOException e) { /* log */ } });
}
```

## `walk` vs `walkFileTree`
| | `walk` | `walkFileTree` |
|---|---|---|
| Style | Stream | Visitor |
| Per-entry error handling | Awkward | First-class via `visitFileFailed` |
| Skip a subtree | Filter | Return `SKIP_SUBTREE` from `preVisitDirectory` |
| Easy to recurse-delete | Sort reverse | First-class via `postVisitDirectory` |

Use `walkFileTree` when you need fine-grained control; `walk` for "show me
all files matching X."

## Run
```bash
cd src
java Basics.FileIO.FilesWalkAndList
```

## See also
- `FilesAndPaths.java` — `Path` and `Files` basics.
- `BufferedStreams.java` — `Files.lines` returns a buffered stream.
- `WatchServiceDemo.java` — react to changes after a walk.
