package Phase7_Concurrency.Multithreading;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Race Condition, Starvation, Livelock, Deadlock
 * ----------------------------------------------
 * The four classic concurrency hazards.
 *
 *
 * 1) RACE CONDITION
 *    Two threads access shared state, at least one writes, with no
 *    synchronization. The final result depends on interleaving — often
 *    silently wrong.
 *
 *      Example: counter++  (read, +1, write — three steps, not atomic).
 *
 *
 * 2) DEADLOCK
 *    Two or more threads each hold a lock the others need. None ever
 *    progresses. See DeadlockDemo.java for a full treatment.
 *
 *
 * 3) LIVELOCK
 *    Threads ARE doing work — but the work is "polite reaction" to each
 *    other and nothing real progresses. Imagine two people in a hallway
 *    each stepping aside the same direction.
 *
 *
 * 4) STARVATION
 *    A thread is technically eligible to run but is repeatedly passed
 *    over because higher-priority / busier threads keep grabbing the
 *    resource it needs. Fairness is the cure.
 *
 *
 * Cousin: PRIORITY INVERSION
 *    A low-priority thread holds a lock a high-priority thread needs,
 *    but a medium-priority thread keeps preempting the low one, so the
 *    high one is "indirectly starved." Less common in JVM-land but still
 *    possible.
 */

public class RaceConditionStarvationLivelock {

    public static void main(String[] args) throws InterruptedException {

        section("1) Race condition — naked counter");
        int[] count = { 0 };
        Thread r1 = new Thread(() -> { for (int i = 0; i < 100_000; i++) count[0]++; });
        Thread r2 = new Thread(() -> { for (int i = 0; i < 100_000; i++) count[0]++; });
        r1.start(); r2.start(); r1.join(); r2.join();
        System.out.println("count = " + count[0] + "   (expected 200000)");
        System.out.println("Fix: use AtomicInteger, synchronized, or a Lock");

        section("2) Race condition — fixed with AtomicInteger");
        AtomicInteger a = new AtomicInteger();
        Thread a1 = new Thread(() -> { for (int i = 0; i < 100_000; i++) a.incrementAndGet(); });
        Thread a2 = new Thread(() -> { for (int i = 0; i < 100_000; i++) a.incrementAndGet(); });
        a1.start(); a2.start(); a1.join(); a2.join();
        System.out.println("atomic = " + a.get());

        section("3) Livelock — polite friends in a hallway");
        Friend alice = new Friend("Alice");
        Friend bob   = new Friend("Bob");
        Thread tA = new Thread(() -> alice.passBatonTo(bob));
        Thread tB = new Thread(() -> bob.passBatonTo(alice));
        tA.start(); tB.start();
        tA.join(2_000); tB.join(2_000);
        System.out.println("(threads possibly still 'passing' — that's livelock)");
        alice.stop = true;
        bob.stop = true;
        tA.join(); tB.join();

        section("4) Starvation — unfair lock");
        ReentrantLock unfair = new ReentrantLock(/*fair*/ false);
        runContention("UNFAIR", unfair);

        section("5) Starvation cure — fair lock");
        ReentrantLock fair = new ReentrantLock(/*fair*/ true);
        runContention("FAIR",   fair);

        section("done");
    }

    /** Mutual politeness produces no progress. */
    static class Friend {
        final String name;
        volatile boolean hasBaton = true;
        volatile boolean stop;
        Friend(String name) { this.name = name; }
        void passBatonTo(Friend other) {
            while (!stop) {
                if (other.hasBaton) {
                    // "After you", says everyone, forever.
                    Thread.onSpinWait();
                    continue;
                }
                // hand it over
                other.hasBaton = true;
                this.hasBaton  = false;
                // tiny pause for output sanity
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                // immediately try to pass it back
                if (hasBaton) continue;
            }
        }
    }

    /** Two pools fight for a single lock — observe how many times each wins. */
    private static void runContention(String label, ReentrantLock lock) throws InterruptedException {
        int[] wins = new int[2];
        Thread[] hot = new Thread[8];
        for (int i = 0; i < hot.length; i++) {
            final int idx = i;
            hot[i] = new Thread(() -> {
                for (int k = 0; k < 5_000; k++) {
                    lock.lock();
                    try { wins[idx % 2]++; }
                    finally { lock.unlock(); }
                }
            });
            hot[i].start();
        }
        for (Thread t : hot) t.join();
        System.out.println(label + " wins: side0=" + wins[0] + ", side1=" + wins[1]
                + (label.equals("UNFAIR") ? "  (often lopsided)" : "  (balanced)"));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
