# `Phaser` (Java 7+)

The flexible cousin of `CyclicBarrier` and `CountDownLatch`. A `Phaser`:
- Has a **varying** number of registered parties (`register` / `arriveAndDeregister`).
- Has a **phase** counter that increases each round.
- Lets each party arrive with several semantics.
- Has a hook to control termination and per-phase actions.

## Methods (cheat sheet)
| Method | Effect |
|---|---|
| `register()` | Add a party |
| `bulkRegister(n)` | Add `n` parties |
| `arriveAndAwaitAdvance()` | "I'm here, wait for everyone else" |
| `arrive()` | "I'm here, don't wait" — returns the current phase |
| `arriveAndDeregister()` | "I'm here, drop me" |
| `awaitAdvance(phase)` | Block until the phase moves past `phase` |
| `getPhase()` | Current phase number |
| `getRegisteredParties()` | Currently registered count |
| `isTerminated()` | Has the phaser terminated? |
| `onAdvance(phase, registered)` (override) | Run an action at each phase boundary; return `true` to terminate |

## When to pick `Phaser` over `CyclicBarrier`
- Party count **changes** across phases (workers join or leave).
- You want a **termination hook** (e.g., terminate after K phases or when all deregister).
- You want **non-blocking arrivals** (`arrive()` returns immediately).

## Default termination
A `Phaser` terminates when **all parties deregister** — which is what
`onAdvance` returns `true` for by default. After that every `arrive`/`await`
is a no-op.

## Common patterns

**Fixed parties, multiple phases:**
```java
Phaser phaser = new Phaser(N);
for (int i = 0; i < N; i++) new Thread(() -> {
    for (int p = 0; p < PHASES; p++) {
        doWorkForPhase(phaser.getPhase());
        phaser.arriveAndAwaitAdvance();
    }
    phaser.arriveAndDeregister();
}).start();
```

**Dynamic joining:**
```java
Phaser phaser = new Phaser(1);  // controller counts as one
spawnWorker(phaser);            // calls phaser.register() before starting
// later:
phaser.arriveAndDeregister();   // controller bows out
```

## `Latch` vs `Barrier` vs `Phaser`
| | `CountDownLatch` | `CyclicBarrier` | `Phaser` |
|---|---|---|---|
| Reusable | No | Yes | Yes |
| Party count | Implicit (count) | Fixed | Variable |
| Phase action | No | One | Per-phase via `onAdvance` |
| Non-blocking arrival | n/a | No | `arrive()` |
| Dynamic join/leave | No | No | Yes |

## Run
```bash
cd src
java Basics.Multithreading.PhaserDemo
```

## See also
- `CountDownLatchDemo.java`, `CyclicBarrierDemo.java`, `SemaphoreDemo.java`.
- `StructuredConcurrency.java` (Java 21) — handles task groups in a different way.
