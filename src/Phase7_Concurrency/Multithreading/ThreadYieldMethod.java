package Phase7_Concurrency.Multithreading;

/**
 * Thread.yield()
 * --------------
 * A HINT to the scheduler that the current thread is willing to give up
 * its current time slice. It is a STATIC method.
 * <p>
 *
 *      Thread.yield();
 * <p>
 *
 * What it really does
 * -------------------
 *   - The thread stays RUNNABLE — it does not enter WAITING.
 *   - The scheduler MAY pick another runnable thread, of the same or
 *     higher priority, to run next. Or it MAY ignore the hint entirely.
 *   - There is NO guaranteed effect. The JVM spec gives no promise of
 *     ordering or fairness.
 *   - It does NOT release locks. It does NOT block.
 * <p>
 *
 * When to use yield
 * -----------------
 * Honestly, almost never in modern code:
 *   - Most production code uses synchronizers, queues, latches, or
 *     park()/unpark() — which actually coordinate.
 *   - yield is a "be polite" hint, not a synchronization primitive.
 * <p>
 *
 * Two niche real uses:
 *   1. Long-running CPU loops in single-threaded benchmarks where you
 *      want to encourage the OS to schedule other work.
 *   2. Implementing a custom spin-wait alongside Thread.onSpinWait()
 *      (Java 9+) — yield is the bigger hammer, onSpinWait is the lighter
 *      CPU-pipeline hint.
 * <p>
 *
 * yield vs sleep vs wait
 * ----------------------
 *                   yield()       sleep(ms)        wait()
 *                   ---------     ---------        ----------
 *   State after     RUNNABLE      TIMED_WAITING    WAITING
 *   Releases locks? No            No               YES (the monitor)
 *   Timed?          —             Yes              Optional
 *   Wakes by        scheduler     timeout/interrupt  notify/interrupt
 */

public class ThreadYieldMethod {

    public static void main(String[] args) throws InterruptedException {

        section("1) yield is a HINT — observe interleaving");
        Runnable shoutyLoop = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("  " + Thread.currentThread().getName() + " i=" + i);
                Thread.yield();                  // be polite, let others run
            }
        };
        Thread a = new Thread(shoutyLoop, "A");
        Thread b = new Thread(shoutyLoop, "B");
        a.start(); b.start();
        a.join();  b.join();

        section("2) Without yield, output may be more clumpy");
        Runnable greedyLoop = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("  " + Thread.currentThread().getName() + " i=" + i);
            }
        };
        Thread c = new Thread(greedyLoop, "C");
        Thread d = new Thread(greedyLoop, "D");
        c.start(); d.start();
        c.join();  d.join();

        section("3) Compare states");
        Thread sleeper = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }, "sleeper");
        sleeper.start();
        Thread.sleep(50);
        System.out.println("sleeper state = " + sleeper.getState());     // TIMED_WAITING
        Thread yielder = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) Thread.yield();
        }, "yielder");
        yielder.start();
        Thread.sleep(50);
        System.out.println("yielder state = " + yielder.getState());     // RUNNABLE
        yielder.interrupt();
        yielder.join();
        sleeper.join();

        section("4) onSpinWait — the modern alternative for tight loops");
        // Use inside a busy loop checking a flag. Slows nothing down but
        // gives the CPU a hint that it's a spin.
        boolean[] flag = { false };
        Thread spinner = new Thread(() -> {
            while (!flag[0]) Thread.onSpinWait();
            System.out.println("spinner saw the flag");
        }, "spinner");
        spinner.start();
        Thread.sleep(50);
        flag[0] = true;
        spinner.join();

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
