# Built-In Annotations

The annotations every Java developer should recognise.

## Compiler / language family
| Annotation | Effect |
|---|---|
| `@Override` | "I'm overriding a superclass method." Compile error if signature doesn't match. |
| `@Deprecated(since = "1.5", forRemoval = true)` | "Going away. Don't add new uses." |
| `@SuppressWarnings("unchecked", "rawtypes", ...)` | Silence named warnings in the smallest scope. |
| `@SafeVarargs` | Generic-varargs method that won't cause heap pollution. |
| `@FunctionalInterface` | Interface error if it has more than one abstract method. |

## Meta-annotation family (you put these *on* your annotations)
| Annotation | Purpose |
|---|---|
| `@Retention` | `SOURCE` / `CLASS` / `RUNTIME` |
| `@Target` | Where it can appear: `METHOD`, `FIELD`, `TYPE`, … |
| `@Inherited` | Subclasses inherit it from a class |
| `@Repeatable` | Allow the same annotation more than once |
| `@Documented` | Include in javadoc |

## Cheatsheet — when to use which
| Situation | Use |
|---|---|
| About to override `equals` / `hashCode` / etc. | **`@Override`** — catches typos |
| Removing an old public method | **`@Deprecated(forRemoval = true)`** |
| Compiler complains and you've manually verified safety | **`@SuppressWarnings`** scoped to the smallest unit |
| Generic varargs method (`<T> List<T> of(T... x)`) | **`@SafeVarargs`** |
| One-method interface for a lambda target | **`@FunctionalInterface`** |

## Use the smallest scope
`@SuppressWarnings` placed on a class silences inside *every* method. Prefer
the method or local variable level so legitimate new warnings still fire.

## Run
```bash
cd src
java Basics.Annotations.BuiltInAnnotations
```

## See also
- `CustomAnnotations.java` — declare your own.
- `RuntimeAnnotations.java` — read them at runtime.
- `Basics/Generics/HeapPollutionAndSafeVarargs.java` — full `@SafeVarargs` treatment.
