# `java.util.concurrent.atomic`

Lock-free counters and references built on **compare-and-set (CAS)** — a
hardware instruction that atomically updates memory only if it still holds
the expected old value.

## What CAS gets you (vs `synchronized`)
| | `Atomic*` | `synchronized` |
|---|---|---|
| Blocking | No | Yes (waits for monitor) |
| Deadlock risk | None | Real |
| Multi-variable atomic update | No | Yes |
| Throughput under low contention | Fast | Fine |
| Throughput under high contention | Spin / CAS-retry can hurt — see `LongAdder` | Often degrades to context-switching |

## The family at a glance
| Class | What it is |
|---|---|
| `AtomicBoolean` / `AtomicInteger` / `AtomicLong` | Single primitive cell |
| `AtomicReference<V>` | Single object reference |
| `AtomicIntegerArray` / `AtomicLongArray` / `AtomicReferenceArray<V>` | Element-wise atomic on an array |
| `AtomicStampedReference<V>` | Reference + integer **stamp** (defeats ABA) |
| `AtomicMarkableReference<V>` | Reference + boolean **mark** |
| `LongAdder` / `DoubleAdder` | **High-throughput** counter (striped cells) |
| `LongAccumulator` / `DoubleAccumulator` | Adder + custom binary op (max, sum, etc.) |
| `*FieldUpdater` | Reflection-based updates of volatile fields |

## Key methods on `AtomicInteger`
| Method | Effect |
|---|---|
| `get()` / `set(v)` | Plain read / write |
| `incrementAndGet()` / `decrementAndGet()` / `addAndGet(d)` | Atomic +1/-1/+d returning the new value |
| `getAndIncrement()` / `getAndAdd(d)` | Returns the **old** value |
| `compareAndSet(expect, update)` | The CAS primitive |
| `updateAndGet(IntUnaryOperator)` | CAS loop with a lambda |
| `accumulateAndGet(x, IntBinaryOperator)` | CAS loop with two args |
| `lazySet(v)` | Relaxed write — visible eventually |

## When to pick which
| Need | Use |
|---|---|
| Single counter | `AtomicInteger` / `AtomicLong` |
| **Hot** counter under heavy contention | `LongAdder` |
| Max / min / custom reduction | `LongAccumulator` |
| Immutable snapshot reference | `AtomicReference<V>` |
| Defeat ABA | `AtomicStampedReference<V>` |
| Multi-variable atomic update | A **lock**, or wrap them in a single immutable object behind an `AtomicReference` |

## The ABA problem
CAS only checks "is the value still X?" — not "has it been X, then Y, then X
again." For lock-free data structures (Treiber stack, etc.), that matters.
`AtomicStampedReference` ties an ever-increasing stamp to the value so the
CAS includes "and the stamp hasn't changed."

## Patterns

**Lazy initialisation:**
```java
AtomicReference<Config> ref = new AtomicReference<>();
Config get() {
    Config c = ref.get();
    if (c == null) {
        c = load();
        if (!ref.compareAndSet(null, c)) c = ref.get();   // someone beat us
    }
    return c;
}
```

**One-shot flag:**
```java
AtomicBoolean started = new AtomicBoolean();
if (started.compareAndSet(false, true)) { /* I'm the one that started it */ }
```

## Run
```bash
cd src
java Basics.Multithreading.AtomicVariables
```

## See also
- `VolatileKeyword.java` — visibility without atomicity.
- `JavaSynchronization.java` — what to use when atomic isn't enough.
- `ThreadSafety.java` — picking the right strategy.
