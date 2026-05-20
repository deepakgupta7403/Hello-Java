# Inner (non-static member) Classes

A nested class **not** marked `static`. Every instance carries a hidden
reference to an instance of the enclosing class, letting it access outer
fields and methods directly.

```java
public class Outer {
    private int n;
    public class Inner {
        int twice() { return n * 2; }     // n is on the outer
    }
}

Outer o = new Outer();
Outer.Inner i = o.new Inner();            // note: outer.new Inner()
```

## Properties
- Implicit field `Outer.this` referencing the enclosing instance.
- Reads / writes outer fields directly (even `private`).
- **Hidden reference keeps the outer alive for GC** — common leak source.
- Cannot have static members (other than `static final` compile-time constants).

## `Outer.this`
When inner and outer have a field with the same name:
```java
public class Outer {
    int n = 1;
    public class Inner {
        int n = 99;
        void show() {
            System.out.println(this.n);          // 99
            System.out.println(Outer.this.n);    // 1
        }
    }
}
```

## Legitimate use cases
- Iterator implementations that read the enclosing collection.
- Adapters tied to a single outer instance.
- Observer / listener patterns where the listener really *is* part of the
  observed object's identity.

## Leak risk
If you hand the inner instance to a long-lived listener registry, **the outer
instance is also pinned**. Common bug in GUI / Android code. Fix: make the
nested class `static` and store an explicit field for whatever you need.

## Decision rule
> **Default to `static` nested.** Only drop the `static` if the inner truly
> needs the enclosing instance.

## Construction syntax
- From outside: `outer.new Inner()`.
- From inside (a non-static method on `Outer`): just `new Inner()`.

## Run
```bash
cd src
java Basics.NestedClasses.InnerClass
```

## See also
- `StaticNestedClass.java` — the default choice.
- `LocalAndAnonymousClass.java` — function-scoped relatives.
- `NestedClassesIntroduction.java` — four-way summary.
