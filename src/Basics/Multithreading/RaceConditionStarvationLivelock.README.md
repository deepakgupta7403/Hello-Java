# Race Conditions, Starvation, Livelock (and Deadlock)

The four classic concurrency hazards. Recognise them by symptom, then choose
the right primitive to fix them.

## Race condition
Two threads access shared state with no synchronization; at least one writes.
The result depends on the interleaving and is usually silently wrong.

**Canonical example:** `counter++` — *read, +1, write*. Three steps, not atomic.

**Fixes:**
- `AtomicInteger.incrementAndGet()`
- `synchronized` block / method
- `Lock` / `ReentrantLock`
- Avoid sharing — keep state thread-local

## Deadlock
Each thread holds a lock another thread needs. No one progresses. See
`DeadlockDemo.java` for a full treatment. Coffman conditions:
1. Mutual exclusion
2. Hold-and-wait
3. No pre-emption
4. Circular wait

**Fixes:** lock ordering, timed `tryLock`, single coarse lock, lock-free data structures.

## Livelock
Threads *are* executing — but their actions are mutual reactions and nothing
moves forward. Two people stepping aside in the same direction. Indistinguishable
from deadlock to a user; CPU might be busy though.

**Fixes:** randomised backoff, asymmetric retry, a single arbiter / queue.

## Starvation
A thread is eligible to run but never gets a chance because higher-priority
or simply faster threads keep winning. Symptom: one thread's progress drops to
nearly zero under load.

**Fixes:**
- Fair locks: `new ReentrantLock(true)`, `Semaphore(permits, true)`
- Bounded queues with FIFO ordering
- Move from priority hacks to explicit scheduling

## Priority inversion (cousin)
A low-priority thread holds a lock a high-priority thread wants, but
medium-priority threads keep preempting the low one. The high-priority thread
is *indirectly* starved.

**Fix (in real-time systems):** priority inheritance / ceiling. On standard
JVMs, prefer to design without priority dependencies.

## Quick decision table
| Symptom | Likely diagnosis | First thing to try |
|---|---|---|
| Counter ends up smaller than expected | Race condition | `AtomicInteger` |
| Two threads stop, CPU idle | Deadlock | Order your locks; use `tryLock` |
| Two threads stop, CPU 100% | Livelock | Randomised backoff |
| One worker rarely runs | Starvation | Fair lock / fair queue |

## Run
```bash
cd src
java Basics.Multithreading.RaceConditionStarvationLivelock
```

## See also
- `DeadlockDemo.java`
- `ThreadSafety.java`
- `AtomicVariables.java`
- `ReentrantLockDemo.java` (fair vs unfair)
