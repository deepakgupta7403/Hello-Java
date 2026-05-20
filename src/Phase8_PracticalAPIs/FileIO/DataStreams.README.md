# Data Streams — `DataInputStream` / `DataOutputStream`

Decorators on top of byte streams that read and write Java's **primitive
types** in a portable, fixed-width binary format. Byte order is **big-endian**,
regardless of platform.

## Primitive widths
| Type | Bytes |
|---|---|
| `byte` | 1 |
| `boolean` | 1 |
| `short` / `char` | 2 |
| `int` / `float` | 4 |
| `long` / `double` | 8 |
| `writeUTF` string | 2 (length prefix) + modified-UTF-8 bytes |

## Pattern
```java
try (var out = new DataOutputStream(new FileOutputStream(f))) {
    out.writeInt(42);
    out.writeUTF("hello");
}
try (var in = new DataInputStream(new FileInputStream(f))) {
    int n = in.readInt();
    String s = in.readUTF();
}
```

Reads must happen in the **exact same order** as writes — there is no schema.

## Methods
| Write | Read |
|---|---|
| `writeBoolean` | `readBoolean` |
| `writeByte` / `writeShort` / `writeInt` / `writeLong` | `readByte` / `readShort` / `readInt` / `readLong` |
| `writeFloat` / `writeDouble` | `readFloat` / `readDouble` |
| `writeChar` | `readChar` |
| `writeUTF(String)` | `readUTF()` |

## ⚠️ Caveats
- **Modified UTF-8**, not standard UTF-8. Cross-language interop will trip on supplementary characters and embedded NULs. For real text use a `Reader`/`Writer` with `StandardCharsets.UTF_8`.
- **No schema** — order, types, count are all on you.
- `readInt()` (and friends) throw `EOFException` when the stream ends, **not** -1.
- For real data formats prefer **JSON / Protobuf / Avro / Java serialization**.

## When data streams are still the right tool
- Hand-rolled binary protocol where you control both ends.
- A teaching exercise.
- Tiny, fixed-format scratch files.

## Run
```bash
cd src
java Basics.FileIO.DataStreams
```

## See also
- `ByteStreams.java` — the underlying API.
- `ObjectStreams.java` — write whole objects, not just primitives.
- `OOPSConcepts/SerializationDeserialization/` — full serialization tutorial.
