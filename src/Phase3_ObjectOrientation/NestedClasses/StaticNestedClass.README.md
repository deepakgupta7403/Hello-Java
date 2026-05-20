# Static Nested Classes

A nested class marked `static`. Effectively a top-level class that happens to
live inside another for namespace reasons.

```java
public class Owner {
    public static class Helper { ... }
}
Owner.Helper h = new Owner.Helper();   // no Owner instance needed
```

## Properties
- **No reference** to an instance of the outer class.
- Can access the outer class's **private static** members.
- Can be `private`, hiding the type entirely outside the enclosing file.
- Cheap to instantiate — no hidden field, no enclosing instance to GC-pin.

## Where you'll see them constantly
| Use | Example |
|---|---|
| **Builder pattern** | `Pizza.Builder` |
| **DTOs / value records** | `public static record Point(int x, int y) {}` |
| **Enum types** | Every `enum` is a static nested class implicitly |
| **Iterators / Spliterators** | Private static helper |
| **Comparator / Strategy** | Tightly bound to the owner |
| **Stateless functional helpers** | `Tuple2<A, B>` |

## Compare with inner classes
| | Static nested | Inner |
|---|---|---|
| Outer reference? | No | Yes (hidden field) |
| Construction | `new Owner.Helper()` | `outer.new Inner()` |
| Can be private? | Yes | Yes |
| Default in modern Java? | **Default** | Only when you really need it |

## Rule of thumb (Effective Java, Item 24)
> **If a member class doesn't require access to an enclosing instance,
> always declare it `static`.**

## Run
```bash
cd src
java Basics.NestedClasses.StaticNestedClass
```

## See also
- `InnerClass.java` — when you actually need the enclosing instance.
- `NestedClassesIntroduction.java` — the four kinds overview.
- `OOPSConcepts/BankingApp/` — uses a nested record pattern.
