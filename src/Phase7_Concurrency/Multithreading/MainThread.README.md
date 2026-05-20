# The Main Thread

## What it is
When you launch a Java program, the JVM creates a thread named **`main`** and
invokes your `public static void main(String[])` on it. Everything else runs
on that thread until you spawn another one.

## Properties
| Property | Default value |
|---|---|
| Name | `main` |
| Thread group | `main` |
| Daemon? | `false` (keeps the JVM alive) |
| Priority | `5` (`Thread.NORM_PRIORITY`) |
| Uncaught handler | Prints stack trace to `System.err` |

## When does the JVM exit?
The JVM exits when **all non-daemon (user) threads** have finished. So:
- If `main` is the only non-daemon thread, the JVM exits when `main()` returns.
- If `main` spawned other non-daemon threads, the JVM waits for *them* too.
- Calling `System.exit(code)` kills the JVM immediately regardless of who's alive.

## Other threads the JVM runs
Even a Hello-World program is multithreaded under the hood:
| Thread | Role |
|---|---|
| `Reference Handler` | Drives `Reference` / `Cleaner` |
| `Finalizer` | Runs `finalize()` (deprecated) |
| `Signal Dispatcher` | Delivers OS signals |
| `Common-Cleaner` | `java.lang.ref.Cleaner` workers |
| `Notification Thread` | JMX |
| GC threads | Depends on the collector (G1, ZGC, Shenandoah, ...) |

All are **daemons**, so they don't keep the JVM alive on their own.

## What you can do with `main` that's useful
- **Rename it** — `Thread.currentThread().setName("app-main")` makes log lines clearer.
- **Install a default uncaught exception handler** with `Thread.setDefaultUncaughtExceptionHandler` so background-thread crashes don't silently disappear.
- **Snapshot all threads** via `Thread.getAllStackTraces()` for crash diagnostics.

## Don't
- Don't `Thread.currentThread().interrupt()` on `main` from inside `main` unless you're handling the flag immediately — leaving it set will surprise downstream library code.

## Run
```bash
cd src
java Basics.Multithreading.MainThread
```

## See also
- `DaemonThread.java` — daemon vs user threads.
- `Threads.java` — general `Thread` API.
