# Java Platform Module System (JPMS) — Introduction

Java 9's big architectural change. Adds an explicit, compile-time unit called
a **module**: a named collection of related packages plus a descriptor
`module-info.java`.

## Why modules
- **Replace** the public / package-private duality with explicit `exports`.
- **Strong encapsulation** — you can't reflect into another module unless it
  `opens` the package to you.
- **Reliable configuration** — `requires` declares dependencies the linker /
  runtime verifies up front.
- **Scalable JDK** — the JDK itself is ~100 modules. `jlink` builds a custom
  runtime image with only what your app needs.

## `module-info.java` cheat sheet
```java
module com.example.app {
    requires java.net.http;                    // I depend on this
    requires transitive java.sql;              // also exposed to my consumers
    requires static lombok;                    // compile-time only

    exports com.example.app.api;               // public API
    exports com.example.app.internal to com.example.tests;

    opens com.example.app.entity;              // allow reflection
    opens com.example.app.entity to spring.core;

    provides com.example.api.SpiPort
         with com.example.app.SpiImpl;         // I implement this SPI

    uses com.example.api.SpiPort;              // I consume a SPI
}
```

## `requires` flavours
| | |
|---|---|
| `requires X` | Compile + runtime dependency |
| `requires static X` | Compile-time only (optional at runtime) |
| `requires transitive X` | Re-exported to my consumers |

## `exports` vs `opens`
| | `exports` | `opens` |
|---|---|---|
| Compile / runtime access | ✅ | ✅ |
| Reflection (`setAccessible`) | ❌ | ✅ |
| Use case | Public API | "Spring needs to inject into my private fields" |

Limit to specific consumers with `... to module.name`.

## Three kinds of modules
| Kind | What it is |
|---|---|
| **Named module** | Has `module-info.java` |
| **Automatic module** | A plain jar on the **module path**; name derived from filename / `Automatic-Module-Name` manifest. Exports *all* packages, reads everything. |
| **Unnamed module** | The classpath. Reads everything. Reverts to "no modules" behaviour. |

## Status of this repo
The `HelloJava` project is **intentionally not modularised**. Modules add
ceremony that fights with the per-folder tutorial layout. This section is
**reference material** — study the syntax, run `jdeps` to explore JDK
modules, and apply the ideas to your own projects.

## Compile / run / link recipes
```bash
# Compile (multi-module project)
javac -d out --module-source-path src $(find src -name '*.java')

# Run
java --module-path out -m com.example.app/com.example.app.Main

# Custom runtime image
jlink --module-path "$JAVA_HOME/jmods":out \
      --add-modules com.example.app \
      --launcher app=com.example.app/com.example.app.Main \
      --output myapp-runtime
```

## Diagnose
```bash
jdeps --module-path out myapp.jar
jdeps --generate-module-info ./mods legacy.jar
```

## Common JDK modules to know
| Module | Contents |
|---|---|
| `java.base` | `java.lang`, `java.util`, `java.io`, `java.nio` (no `requires` needed) |
| `java.sql` | JDBC types |
| `java.net.http` | HTTP/2 + WebSocket client |
| `java.logging` | `java.util.logging` |
| `java.management` | JMX |
| `java.compiler` | The `javac` API |

## Run this file
```bash
cd src
java Basics.Modules.ModulesIntroduction
```
(Prints the module info for the current unnamed module + lists JDK modules.)

## See also
- `ModuleExamples.java` — `module-info.java` snippets.
- `ServiceLoaderDemo.java` — `provides` / `uses` in action.
