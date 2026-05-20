# `Optional<T>`

A container that holds either a value or no value. Used to make "this method
may not return anything" **explicit in the type system** — removes the trap of
forgetting to null-check.

```java
Optional<User> u = repo.find(id);
u.ifPresent(this::greet);
```

## When `Optional` earns its keep
- **Return values** when "nothing" is a legitimate result.
- **Stream terminal ops** that may find nothing — `findFirst`, `findAny`, `max`, `min`, `reduce`.

## When it doesn't (anti-patterns)
| Smell | Fix |
|---|---|
| Optional field | Use a real default or a clearly-nullable field |
| Optional parameter | Take a value and validate; overload methods for the "absent" case |
| `Optional<Collection>` | Return an **empty** collection instead |
| Optional in `Map` values | Use `Map.getOrDefault` / `computeIfAbsent` |
| `Optional.get()` without an `isPresent()` check | Use `orElse`, `orElseThrow`, `ifPresent`, or `map` |

`Optional` is heap-allocated and not free. Using it everywhere is a smell.

## API at a glance
| Method | Purpose |
|---|---|
| `of(t)` | Wrap (throws on null) |
| `ofNullable(t)` | Wrap, nulls become `empty()` |
| `empty()` | An empty Optional |
| `isPresent()` / `isEmpty()` | Probe |
| `get()` | Extract — **throws** if empty (avoid) |
| `orElse(default)` | Extract or return `default` |
| `orElseGet(Supplier)` | Lazy default |
| `orElseThrow()` / `orElseThrow(Supplier)` | Extract or throw |
| `ifPresent(Consumer)` | Side-effect if present |
| `ifPresentOrElse(Consumer, Runnable)` | Java 9+ either-branch |
| `map(Function)` | Transform if present |
| `flatMap(Function<T, Optional<R>>)` | Compose options |
| `filter(Predicate)` | Conditional |
| `or(Supplier<Optional<T>>)` | Java 9+ fallback Optional |
| `stream()` | Java 9+ — 0-or-1 element stream |

## Primitive specialisations
`OptionalInt`, `OptionalLong`, `OptionalDouble` — avoid boxing and pair with
`IntStream` / `LongStream` / `DoubleStream`. Each has its own `getAsInt()` /
`getAsLong()` / `getAsDouble()`.

## Idiomatic patterns

**Flatten a stream of Optionals:**
```java
streamOfOptionals.flatMap(Optional::stream)        // Java 9+
```

**Chain transformations:**
```java
return repo.find(id)
           .map(User::email)
           .map(String::toLowerCase)
           .filter(e -> e.endsWith("@example.com"))
           .orElseThrow(() -> new NotFound(id));
```

**Domain choice:** return `Optional<User>` from your `find`, but `getById` /
`requireById` throws when the user must exist. Two methods, two intents.

## Run
```bash
cd src
java Basics.OptionalType.OptionalDemo
```

## See also
- `Basics/ExceptionHandling/NullPointerExceptions.java` — the broader NPE story.
- `Basics/LambdaAndStreams/TerminalOperations.java` — `findFirst`, `min`, `max` return `Optional`.
