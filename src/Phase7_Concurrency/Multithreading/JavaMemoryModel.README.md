# Java Memory Model (JMM)

The formal specification of how reads and writes from different threads
interact. Without it, a program is at the mercy of the JIT, CPU memory
subsystem, and OS scheduler — each of which may reorder or cache memory
operations.

## Two concerns
| Concern | Question it answers |
|---|---|
| **Atomicity** | Is the read/write a single indivisible step? |
| **Visibility** | When does a thread see another thread's write? |

(64-bit `long`/`double` reads & writes are *not guaranteed atomic* on the JVM
spec without `volatile`, though all modern 64-bit hardware actually does it.)

## Happens-before edges (the ones you'll actually use)
1. **Program order** — within a single thread, source order is preserved.
2. **Monitor** — unlock → subsequent lock of the *same* monitor.
3. **Volatile** — write → subsequent read of the *same* volatile field.
4. **Thread start** — `t.start()` happens-before the first action of `t`.
5. **Thread join** — last action of `t` happens-before `t.join()` returning.
6. **Final fields** — once the constructor returns, final fields of a properly
   constructed object are visible to every thread that obtains a reference.
7. **Transitivity** — A→B and B→C implies A→C.

If no chain of these connects two cross-thread actions, the JVM is allowed to
reorder or hide them. Bugs you'll see:
- Reader sees stale data forever (classic spinning-on-flag loop).
- Reader sees the *new* value of one field but the *old* value of another.
- Reader sees a partially-initialised object after an unsafe publication.

## Safe publication
Five canonical ways to publish an object so other threads see it intact:
- Initialise in a static initializer.
- Store into a **`final`** field of a properly constructed object.
- Store into a **`volatile`** field (or `AtomicReference`).
- Store into a field protected by a lock.
- Hand off through a thread-safe container (e.g. `ConcurrentHashMap.put`).

## Quick decision table
| Need | Use |
|---|---|
| Visibility-only of a flag | `volatile` |
| Atomic counter / CAS | `AtomicInteger`, `LongAdder` |
| Multi-field invariant | `synchronized` or `Lock` |
| Immutable value safely shared | `record` / `final` fields |
| Hand-off between threads | `BlockingQueue`, `CompletableFuture` |

## Trivia
- The current JMM was specified in **JSR-133** (Java 5). The Java 1.4 model
  was *too weak* — even `final` fields could be observed mid-construction.
- A "memory barrier" / "fence" is the underlying machine concept; the JMM
  hides it behind `synchronized`, `volatile`, `Atomic*`, and the `VarHandle`
  API.

## Run
```bash
cd src
java Basics.Multithreading.JavaMemoryModel
```

## See also
- `VolatileKeyword.java`
- `AtomicVariables.java`
- `JavaSynchronization.java`
- `ThreadSafety.java`
