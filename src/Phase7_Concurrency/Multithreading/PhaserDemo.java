package Phase7_Concurrency.Multithreading;

import java.util.concurrent.Phaser;

/**
 * Phaser (Java 7+)
 * ----------------
 * The flexible cousin of CyclicBarrier and CountDownLatch. A Phaser:
 *
 *   - Has a varying number of REGISTERED parties (register / arriveAndDeregister).
 *   - Has a PHASE counter that increases each round.
 *   - Lets parties signal arrival with several methods:
 *
 *           arriveAndAwaitAdvance()    - "I'm here; wait for the rest"
 *           arrive()                    - "I'm here; don't wait" (returns the phase)
 *           arriveAndDeregister()       - "I'm here; drop me from the pool"
 *           awaitAdvance(phase)         - block until we move past `phase`
 *
 *   - Supports a HOOK: override onAdvance(phase, registered) to control
 *     when termination occurs and run per-phase actions.
 *
 *
 * When to pick Phaser over CyclicBarrier
 * --------------------------------------
 *   - Party count CHANGES across phases.
 *   - You want a hook that decides when to terminate (e.g. when all
 *     parties have deregistered, or after K phases).
 *   - You want non-blocking arrivals (arrive() returns immediately).
 *
 *
 * Termination
 * -----------
 * A Phaser terminates when onAdvance returns true. By default it
 * terminates when all parties deregister. Once terminated, every
 * arrive/await returns immediately (no more synchronisation).
 */

public class PhaserDemo {

    public static void main(String[] args) throws InterruptedException {

        section("1) Fixed parties, multiple phases");
        int N = 3;
        Phaser phaser = new Phaser(N) {
            @Override protected boolean onAdvance(int phase, int registered) {
                System.out.println("  >>> end of phase " + phase + ", still " + registered + " parties <<<");
                return registered == 0;            // terminate when all deregister (default)
            }
        };
        Thread[] ts = new Thread[N];
        for (int i = 0; i < N; i++) {
            final int id = i;
            ts[i] = new Thread(() -> {
                for (int p = 0; p < 3; p++) {
                    System.out.println("  party " + id + " in phase " + phaser.getPhase());
                    sleep(30 + 10 * id);
                    phaser.arriveAndAwaitAdvance();
                }
                phaser.arriveAndDeregister();
            }, "p-" + id);
        }
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();
        System.out.println("phaser terminated? " + phaser.isTerminated());

        section("2) Parties JOINING and LEAVING during execution");
        Phaser dynamic = new Phaser(1);            // main is party #1
        // Start with 2 workers
        startWorker(dynamic, "early-A");
        startWorker(dynamic, "early-B");
        Thread.sleep(20);
        // Add a third mid-flight
        startWorker(dynamic, "late-C");
        // main waits for them
        dynamic.arriveAndDeregister();             // main says it's done
        while (!dynamic.isTerminated()) sleep(20);
        System.out.println("dynamic phaser terminated");

        section("3) arrive() — fire-and-forget arrival");
        Phaser p = new Phaser(2);
        Thread fast = new Thread(() -> { System.out.println("  fast arrives"); p.arrive(); }, "fast");
        Thread slow = new Thread(() -> {
            sleep(30);
            System.out.println("  slow arrives + waits");
            p.arriveAndAwaitAdvance();
        }, "slow");
        fast.start(); slow.start();
        fast.join();  slow.join();
        System.out.println("phase advanced to " + p.getPhase());

        section("done");
    }

    private static void startWorker(Phaser phaser, String name) {
        phaser.register();
        new Thread(() -> {
            for (int p = 0; p < 2; p++) {
                System.out.println("  " + name + " in phase " + phaser.getPhase());
                sleep(20);
                phaser.arriveAndAwaitAdvance();
            }
            phaser.arriveAndDeregister();
        }, name).start();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
