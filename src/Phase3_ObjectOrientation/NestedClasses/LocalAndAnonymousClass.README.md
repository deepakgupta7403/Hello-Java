# Local and Anonymous Classes

Two ways to declare a class **inside a method** or block.

| | Local | Anonymous |
|---|---|---|
| Named? | Yes | No |
| Instantiations | Many | Exactly one (at declaration) |
| Extend / implement | Either, any number of interfaces | One class **or** one interface |
| Modifiers | None of `public` / `protected` / `private` | Same |
| Static members | Only compile-time constants | Only compile-time constants |
| Java 16+ | Local **records / enums** allowed | n/a |

## Capture rules (both kinds)
| Source | Visibility |
|---|---|
| Outer **fields** | Read / write |
| Outer **local variables** | Read-only, must be **effectively final** |
| Enclosing `this` | `Outer.this` |

"Effectively final" = assigned once, never reassigned. The compiler enforces.

## Local class skeleton
```java
void doStuff() {
    class Helper { ... }
    new Helper().something();
}
```
Use when you want:
- A name (for stack traces).
- Multiple instances.
- A constructor with arguments.

## Anonymous class skeleton
```java
Runnable r = new Runnable() {
    @Override public void run() { ... }
};
```
Use when:
- One-shot override at the call site.
- You need a name for `this` (lambdas' `this` is the *enclosing* instance, not the lambda itself).
- The target is **not** a functional interface (multiple abstract methods, or overrides of `equals`/`hashCode`/`toString`).

## Lambda vs anonymous class
| | Lambda | Anonymous |
|---|---|---|
| Target type | Functional interface only | Any class / interface |
| `this` | Enclosing instance | The anonymous instance |
| Captures `this` reference? | Only if you use it | Always (inherits enclosing scope) |
| Instance count | Possibly shared | Always a new instance |

For functional interfaces, **prefer the lambda**.

## Run
```bash
cd src
java Basics.NestedClasses.LocalAndAnonymousClass
```

## See also
- `StaticNestedClass.java`, `InnerClass.java`.
- `Basics/LambdaAndStreams/LambdaExpressions.java` — what replaced most anon classes.
- `Basics/Methods/MethodReferences.java`.
