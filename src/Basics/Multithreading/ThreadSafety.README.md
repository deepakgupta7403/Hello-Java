# Thread Safety

> A class is **thread-safe** when it behaves correctly when used from multiple
> threads, with no additional synchronization required from the caller.
> — Brian Goetz, *Java Concurrency in Practice*

## Strategies (most to least preferred)
| Strategy | Examples |
|---|---|
| **Immutability** | `String`, `Integer`, `BigDecimal`, records, `LocalDate`, `List.of(...)` |
| **Confinement** | Local variables, `ThreadLocal`, Swing on the EDT |
| **Concurrent containers** | `ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue` |
| **Atomic primitives** | `AtomicInteger`, `LongAdder`, `LongAccumulator` |
| **Explicit locking** | `synchronized`, `ReentrantLock`, `StampedLock` |
| **`volatile`** | Visibility-only for single-writer fields (e.g., stop flag) |

## Levels of safety
| Level | Meaning |
|---|---|
| **Immutable** | Cannot change after construction. Safe forever. |
| **Thread-safe** | All necessary synchronization is internal. Examples: `ConcurrentHashMap`, `AtomicLong`. |
| **Conditionally safe** | Safe if the caller follows a contract (e.g., wrap iteration of `Collections.synchronizedList` in a `synchronized` block). |
| **Thread-compatible** | Works in single-threaded code; caller adds synchronization. `ArrayList`, `HashMap`. |
| **Thread-hostile** | Cannot be made safe; broken by design (rare). |

## Common bug: compound operations
Even a thread-safe container is broken by **check-then-act**:
```java
if (!map.containsKey(k)) map.put(k, v);    // RACE
```
Use the atomic variant instead:
```java
map.putIfAbsent(k, v);
map.computeIfAbsent(k, key -> compute(key));
```

## "Synchronized" vs "concurrent" collections
| | `Collections.synchronizedXxx` | `Concurrent*` |
|---|---|---|
| Strategy | One lock for everything | Lock striping or lock-free |
| Iteration | Throws `CME`; needs external lock | Snapshot or weakly consistent |
| Throughput | Low under contention | High |
| When to use | Tiny needs, legacy code | Modern, real concurrency |

## Counter rule of thumb
- Single writer, many readers → `volatile`.
- Many writers, simple counter → `AtomicInteger` / `LongAdder`.
- Many writers, complex compound state → `synchronized` or `ReentrantLock`.

## Run
```bash
cd src
java Basics.Multithreading.ThreadSafety
```

## See also
- `JavaSynchronization.java` — `synchronized` basics.
- `AtomicVariables.java` — lock-free primitives.
- `VolatileKeyword.java` — visibility-only.
- `RaceConditionStarvationLivelock.java` — names for the bugs you're avoiding.
