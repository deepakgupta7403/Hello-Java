# Snake — Multithreading Project

A playable Swing Snake game that demonstrates the canonical
**simulation thread + UI thread** split with proper shared-state
synchronization.

## Files
| File | Role |
|---|---|
| `SnakeGame.java` | Main runner. Builds the UI on the EDT, starts the game-loop thread, joins it on close. |
| `GameLoop.java` | The simulation thread. Ticks the world, asks the EDT to repaint. |
| `SnakeBoard.java` | Swing `JPanel`. Lives on the EDT. Handles input and paints frames. |
| `GameState.java` | The shared mutable model. Guarded by a `ReentrantLock`. Exposes immutable `Snapshot`s. |
| `Cell.java` | Immutable `(x, y)` record. |
| `Direction.java` | Four-way enum with deltas and opposites. |

## Thread model

```
              ┌──────────────────┐
              │   EDT (Swing)    │
              │  - paint frames  │
              │  - read input    │
              └────────┬─────────┘
                       │ setDirection / snapshot
                       ▼
              ┌──────────────────┐
              │   GameState      │
              │  (ReentrantLock) │
              └────────▲─────────┘
                       │ tick / snapshot
                       │
              ┌────────┴─────────┐
              │  game-loop       │
              │  - sleep tick    │
              │  - state.tick()  │
              │  - invokeLater   │
              └──────────────────┘
```

### Who reads, who writes
| Source | Reads | Writes |
|---|---|---|
| EDT (input) | — | `setDirection`, `reset` |
| EDT (paint) | `snapshot()` | — |
| Game loop | (none) | `tick()` |

Every public `GameState` method takes the same `ReentrantLock`, so reads
and writes never overlap. Painting holds the lock only long enough to
snapshot — the paint itself happens against the snapshot.

## Why this is a good demonstration
- **Mutual exclusion** with `ReentrantLock`.
- **Snapshot pattern** for safe rendering (immutable `record`).
- **Thread responsibility split** — game loop separate from EDT.
- **Cooperative shutdown** — `stop()` + `interrupt()` + `join()` on close.
- **Posting work back to a thread** via `SwingUtilities.invokeLater`.
- **No locks held across calls into foreign code** — the lock is released before `invokeLater`.

## Controls
- `WASD` or arrow keys — turn.
- `R` — restart.
- Close window — clean shutdown.

## Run

From the repo root:
```bash
cd src
java Basics.Multithreading.SnakeGame.SnakeGame
```

Or run `SnakeGame.java` from your IDE.

## Things to try once it runs
1. Slow the loop down (raise `TICK_MS`) and observe that input "queues up" — the next direction is applied on the next tick.
2. Try wrapping the snake instead of dying at walls — what state needs to change?
3. Replace `ReentrantLock` with `synchronized` — same correctness, different ergonomics.
4. Add a "pause" feature using a `Condition` on the lock.
5. Run multiple "AI" snakes — each gets its own thread, but they all share the board.

## See also
- `../JavaSynchronization.java` — the building block for `GameState`.
- `../ReentrantLockDemo.java` — why we use it here.
- `../ThreadInterruption.java` — for the shutdown flow.
- `../ThreadLifecycle.java` — what state the loop thread is in at any moment.
