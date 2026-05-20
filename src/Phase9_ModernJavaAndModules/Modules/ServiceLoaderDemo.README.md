# `ServiceLoader` — the built-in plugin / SPI mechanism

`ServiceLoader` lets you declare a **service interface** and discover
implementations at runtime — without hard-coding their names.

## Two pieces
1. A service **interface** (or abstract class).
2. One or more **implementations**, declared so the JVM can find them.

## Declaring providers

### Classpath / unnamed module
A `META-INF/services/<fully-qualified-interface-name>` text file listing
implementation classes, one per line:
```
META-INF/services/com.example.api.Codec
-------------------------------------------
com.example.codec.GzipCodec
com.example.codec.SnappyCodec
```

### Modular project
Declare in `module-info.java`:
```java
module com.example.codec {
    requires com.example.api;
    provides com.example.api.Codec
         with com.example.codec.GzipCodec,
              com.example.codec.SnappyCodec;
}
```

And the consumer module declares:
```java
module com.example.app {
    requires com.example.api;
    uses com.example.api.Codec;
}
```

## Loading at runtime
```java
ServiceLoader<Codec> loader = ServiceLoader.load(Codec.class);
for (Codec c : loader) { /* ... */ }

// Stream form — lets you inspect type before instantiating
Codec chosen = loader.stream()
        .map(ServiceLoader.Provider::get)
        .filter(c -> c.canHandle(input))
        .findFirst()
        .orElseThrow();
```

## Real-world examples
- **JDBC drivers** (`java.sql.Driver`) — historic; now SPI-driven.
- **Logging** (SLF4J, `java.util.spi` providers).
- **Cryptography providers** (`java.security.Provider`).
- **Charsets** (`java.nio.charset.spi.CharsetProvider`).
- **File systems** (`java.nio.file.spi.FileSystemProvider`).

## Why use it
- Decoupling: the consumer doesn't know about specific implementations.
- Plugin architectures: drop a jar on the classpath / module path and a new provider appears.
- Lazy: providers aren't instantiated until you ask.

## Caveats
- **Order** depends on classpath order — don't rely on a specific provider being "first."
- Providers must have a **public no-arg constructor** or a `public static provider()` method.
- In modular projects, `uses` is required on the consumer side; otherwise `ServiceLoader.load` returns nothing.

## Run
```bash
cd src
java Basics.Modules.ServiceLoaderDemo
```

## See also
- `ModulesIntroduction.java`, `ModuleExamples.java`.
- `Basics/Reflection/DynamicInvocation.java` — different shape of dynamic dispatch.
