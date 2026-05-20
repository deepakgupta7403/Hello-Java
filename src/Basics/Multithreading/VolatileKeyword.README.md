# The `volatile` Keyword

A field modifier giving you **memory-model guarantees** — but no mutual
exclusion.

## What it gives you
1. **Visibility** — a write to a `volatile` field is immediately visible to subsequent reads of the same field, on any thread.
2. **Ordering** — actions before a volatile write *happen-before* any subsequent read of that volatile. Release / acquire semantics.
3. **Atomic 64-bit reads/writes** — even on 32-bit JVMs, a `volatile long` or `volatile double` is read/written atomically.

## What it does NOT give you
- Mutual exclusion.
- Atomic compound operations: `volatile int n; n++` is **still racy**.
- Multi-field consistency. Updates to two volatile fields are *each* visible, but the *combination* may be observed mid-update.

## When `volatile` is the right tool
| Scenario | Use `volatile`? |
|---|---|
| Single-writer stop / cancel flag | ✅ |
| Publish a new immutable snapshot reference | ✅ |
| Many writers updating a counter | ❌ — use `AtomicInteger` / `LongAdder` |
| Multi-step state transition | ❌ — use a lock |

## Double-checked locking
The textbook unsafe singleton requires **volatile**:
```java
class Cfg {
    private static volatile Cfg INSTANCE;
    static Cfg get() {
        Cfg c = INSTANCE;
        if (c == null) {
            synchronized (Cfg.class) {
                c = INSTANCE;
                if (c == null) INSTANCE = c = new Cfg();
            }
        }
        return c;
    }
}
```
Without `volatile`, a second thread could see a non-null reference whose
fields are still default values.

## `volatile` vs `synchronized` vs `Atomic`
| | `volatile` | `synchronized` | `Atomic*` |
|---|---|---|---|
| Visibility | ✅ | ✅ | ✅ |
| Mutual exclusion | ❌ | ✅ | ❌ (lock-free) |
| Atomic compound (CAS) | ❌ | ✅ (block) | ✅ |
| Many readers, one writer | Best | Overkill | Overkill |
| Many writers (counter) | Wrong | OK | Best |

## Run
```bash
cd src
java Basics.Multithreading.VolatileKeyword
```

## See also
- `JavaMemoryModel.java` — the underlying happens-before edges.
- `AtomicVariables.java` — when you need atomic compound ops.
- `JavaSynchronization.java` — when you need mutual exclusion.
