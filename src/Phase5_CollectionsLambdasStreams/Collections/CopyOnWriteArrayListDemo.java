package Phase5_CollectionsLambdasStreams.Collections;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * java.util.concurrent.CopyOnWriteArrayList&lt;E&gt;
 * --------------------------------------------
 * A thread-safe List variant where EVERY MUTATION COPIES the underlying
 * array. Iteration walks the SNAPSHOT taken when the Iterator was created -
 * it never sees subsequent writes and never throws
 * ConcurrentModificationException.
 *
 *
 * Cost / Benefit
 * --------------
 *
 *   Reads (get, iterator, contains):    very FAST. No synchronization
 *                                       because the snapshot never changes.
 *
 *   Writes (add, set, remove):          EXPENSIVE. Each one allocates a
 *                                       new internal array.
 *
 *
 * When To Use It
 * --------------
 *   - READ-MOSTLY shared lists: event listeners, observer registrations,
 *     configuration snapshots, plugins.
 *   - When you want simple iteration with no locks and few writers.
 *
 *
 * When NOT To Use It
 * ------------------
 *   - Write-heavy or large collections - the copy cost dominates.
 *   - Single-threaded code - ArrayList is much cheaper.
 *
 *
 * Snapshot Iteration Semantics
 * ----------------------------
 *      List = [A, B, C]
 *      it = list.iterator();          // captures snapshot [A, B, C]
 *      list.add(D);                    // current state becomes [A, B, C, D]
 *      it.next()                       // sees A
 *      it.next()                       // sees B
 *      it.next()                       // sees C
 *      it.hasNext()                    // false - the iterator's snapshot ended
 *
 * Iterator also DOES NOT SUPPORT remove()/set()/add() - the snapshot is
 * immutable.
 *
 *
 * Specific Methods
 * ----------------
 *   addIfAbsent(e)                          - add only if not already present
 *   addAllAbsent(c)                         - bulk addIfAbsent
 *   These two are useful for "set-like" registrations.
 *
 *
 * Cousin Class
 * ------------
 *   CopyOnWriteArraySet                     - the Set version
 */

public class CopyOnWriteArrayListDemo {

    public static void main(String[] args) throws InterruptedException {

        section("1) Basic usage - looks like ArrayList");
        CopyOnWriteArrayList<String> a = new CopyOnWriteArrayList<>();
        a.add("alpha");
        a.add("beta");
        a.addIfAbsent("alpha");             // no-op (already present)
        a.addIfAbsent("gamma");             // added
        System.out.println("list = " + a);

        section("2) Snapshot iterator semantics");
        CopyOnWriteArrayList<Integer> live = new CopyOnWriteArrayList<>(List.of(1, 2, 3));
        Iterator<Integer> snapshot = live.iterator();   // snapshot taken NOW
        live.add(99);
        live.add(100);
        System.out.print("iterator sees: ");
        while (snapshot.hasNext()) System.out.print(snapshot.next() + " ");
        System.out.println();
        System.out.println("list is now : " + live);

        section("3) Iterator does NOT support remove()");
        try {
            live.iterator().remove();
        } catch (UnsupportedOperationException e) {
            System.out.println("iterator.remove() -> UnsupportedOperationException");
        }

        section("4) The textbook use case - thread-safe listener list");
        CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();
        Subject subject = new Subject(listeners);

        listeners.add(msg -> System.out.println("  listener-A got: " + msg));
        listeners.add(msg -> System.out.println("  listener-B got: " + msg));

        // Many concurrent broadcasts and ONE registration in the middle;
        // none of the publishers will get a ConcurrentModificationException.
        Thread publisher = new Thread(() -> {
            for (int i = 0; i < 5; i++) subject.publish("evt-" + i);
        });
        Thread joiner = new Thread(() ->
                listeners.add(msg -> System.out.println("  listener-C (late) got: " + msg)));

        publisher.start();
        joiner.start();
        publisher.join();
        joiner.join();

        section("5) Writes are EXPENSIVE - rough timing");
        final int N = 50_000;
        CopyOnWriteArrayList<Integer> cow = new CopyOnWriteArrayList<>();
        long t = System.nanoTime();
        for (int i = 0; i < N; i++) cow.add(i);
        long ms = (System.nanoTime() - t) / 1_000_000;
        System.out.println("COW add " + N + " items: " + ms + " ms");

        java.util.ArrayList<Integer> al = new java.util.ArrayList<>();
        t = System.nanoTime();
        for (int i = 0; i < N; i++) al.add(i);
        long alMs = (System.nanoTime() - t) / 1_000_000;
        System.out.println("ArrayList add " + N + " items: " + alMs + " ms");

        // OUTPUT (timings vary; the relative ordering tells the story)
    }

    @FunctionalInterface
    interface Listener { void onMessage(String msg); }

    static class Subject {
        private final List<Listener> listeners;
        Subject(List<Listener> listeners) { this.listeners = listeners; }
        void publish(String msg) {
            // No synchronization needed: the iterator is a snapshot.
            for (Listener l : listeners) l.onMessage(msg);
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
