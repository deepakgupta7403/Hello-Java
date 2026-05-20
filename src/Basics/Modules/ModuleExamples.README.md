# Module Examples — sample `module-info.java` shapes

A catalogue of realistic module declarations. Since the project is unnamed
(no `module-info.java`), the snippets live in comments — copy them into a
modular project to use.

## Tiny library
```java
module com.example.utils {
    exports com.example.utils;
}
```

## Library + internal helpers
```java
module com.example.app {
    requires java.net.http;
    requires java.logging;
    exports com.example.app.api;
    // com.example.app.internal is NOT exported — module-private
}
```

## Web app using Spring (needs reflection)
```java
module com.example.webapp {
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.context;

    exports com.example.webapp.controller;
    opens com.example.webapp.controller to spring.core;
    opens com.example.webapp.service    to spring.core;
}
```

## Compile-time-only dependency
```java
module com.example.tooling {
    requires static lombok;            // not required at runtime
    exports com.example.tooling;
}
```

## Transitive dependency (re-exported)
```java
module com.example.core {
    requires transitive org.slf4j;
    exports com.example.core;
}
```

## Service provider + consumer (SPI)
```java
module com.example.payments.api {
    exports com.example.payments.api;
    uses com.example.payments.api.Gateway;
}

module com.example.payments.stripe {
    requires com.example.payments.api;
    provides com.example.payments.api.Gateway
         with com.example.payments.stripe.StripeGateway;
}
```

## Open module (every package implicitly opened)
```java
open module com.legacy.app {
    requires java.sql;
    exports com.legacy.app.api;
}
```
Useful for legacy code that needs full reflection. Don't reach for it
reflexively — name-scoped `opens` is safer.

## Automatic module compatibility
A plain jar lands on the module path as an **automatic module**. Add to
`META-INF/MANIFEST.MF`:
```
Automatic-Module-Name: com.example.legacy
```
…so consumers get a stable name even before you write a real
`module-info.java`.

## Multi-release modules
A jar can ship per-version classes under `META-INF/versions/N/`. Each version
shares the base `module-info.java` (or a per-version variant).

## `jdeps --generate-module-info`
```bash
jdeps --generate-module-info ./mods build/libs/mylib.jar
```
Reads bytecode and prints a stub `module-info.java` listing real dependencies
— a great starting point for modularising a legacy jar.

## Run
```bash
cd src
java Basics.Modules.ModuleExamples
```
(Prints unnamed-module info; the catalogue itself is the comments.)

## See also
- `ModulesIntroduction.java`.
- `ServiceLoaderDemo.java` — `provides` / `uses` in action.
