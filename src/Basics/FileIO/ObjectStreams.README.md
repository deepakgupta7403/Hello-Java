# Object Streams — `ObjectOutputStream` / `ObjectInputStream`

Read and write **whole object graphs** with one call each. The class must
implement `Serializable`; the stream then records every reachable field.

```java
out.writeObject(obj);
Object o = in.readObject();    // throws ClassNotFoundException
```

## `Serializable` contract
- `implements Serializable` (a marker interface).
- Declare a `serialVersionUID`, ideally with `@Serial` (Java 14+).
- Non-serializable fields must be `transient`.
- The whole superclass chain must be `Serializable`, or have a no-arg constructor reachable for deserialization.

## What gets preserved
- Every reachable serializable field.
- **Object identity within the graph** — if two fields point to the same instance, deserialization keeps them pointing to the same instance.
- `transient` fields → default value (null / 0 / false).

## ⚠️ Don't use this for real external data
Java serialization has earned a reputation for:
- **Security holes** — deserialising untrusted bytes can run code via "gadget chains."
- **Versioning fragility** — small field changes break the format.
- **Verbosity and slowness** vs JSON / Protobuf / Avro.

Use it only for **trusted, internal, short-lived** data. For config, APIs,
cross-language: pick JSON, Protobuf, or Avro.

## Modern hardening (Java 9+)
`ObjectInputFilter` lets you allow-list classes that can be deserialized:
```java
in.setObjectInputFilter(info ->
    info.serialClass() == MyType.class
        ? ObjectInputFilter.Status.ALLOWED
        : ObjectInputFilter.Status.REJECTED);
```
Or set globally with `-Djdk.serialFilter=...`.

## When object streams are still useful
- Quickly persisting a tree of internal objects between runs.
- Built-in `Object.clone()` alternative via serialize/deserialize.
- Test fixtures.

## Run
```bash
cd src
java Basics.FileIO.ObjectStreams
```

## See also
- `OOPSConcepts/SerializationDeserialization/` — full serialization tour.
- `DataStreams.java` — primitive-level alternative.
- `ModernFileIO.java` — Java 11+ text shortcuts.
