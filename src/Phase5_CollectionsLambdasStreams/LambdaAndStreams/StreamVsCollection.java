package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream vs Collection - Two Different Things
 * -------------------------------------------
 * Streams and Collections both have iterator() but they serve different
 * purposes. Knowing which to reach for is half the job.
 * <p>
 *
 * Side-by-Side
 * ------------
 * <p>
 *
 *   Aspect             | Collection                | Stream
 *   -------------------+---------------------------+------------------------------
 *   Purpose            | STORE data                | COMPUTE on data
 *   Holds elements?    | Yes - lives in memory     | No - pulls from a source
 *   Eager or lazy?     | EAGER - elements exist    | LAZY - pipeline runs only
 *                      |                           |   when terminal op asks
 *   Iterable multiple? | YES                       | NO - one-shot
 *   Modifiable?        | YES (most)                | n/a - source not mutated
 *   External vs        | External - YOU iterate    | Internal - the stream walks
 *   internal iteration |                           |   itself
 *   Order              | Defined by impl           | May be ordered or not
 *   Parallelism        | Manual (Threads, ES, ...) | One method: .parallel()
 *   Size               | size() always known       | usually unknown / unbounded
 * <p>
 *
 * "Compute" vs "Store" - The Big Idea
 * -----------------------------------
 * A List is a COLLECTION OF EXISTING DATA you can come back to. A Stream
 * is a RECIPE for processing - you describe filter / map / reduce and the
 * runtime walks the source once. After the terminal op, the stream is gone.
 * <p>
 *
 * Crossing the Boundary
 * ---------------------
 *   Collection -&gt; Stream:    list.stream() / set.parallelStream()
 *   Stream     -&gt; Collection: stream.collect(Collectors.toList()) / toSet()
 *                              stream.toList()  (Java 16+, unmodifiable)
 * <p>
 *
 * Common Confusion
 * ----------------
 *   - "I'll keep my Stream and reuse it" - you can't. Create a fresh one.
 *   - "I'll mutate the underlying List from the stream"  - usually a CME.
 *   - "I'll call size() on a Stream" - there's no such method. Use count().
 */

public class StreamVsCollection {

    public static void main(String[] args) {

        section("1) Same query - imperative on a List vs declarative on a Stream");
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6);

        // Imperative
        int totalImp = 0;
        for (int n : nums) if (n % 2 == 0) totalImp += n * n;

        // Declarative
        int totalStream = nums.stream()
                              .filter(n -> n % 2 == 0)
                              .mapToInt(n -> n * n)
                              .sum();

        System.out.println("imperative total = " + totalImp);
        System.out.println("stream     total = " + totalStream);

        section("2) Stream is ONE-SHOT - re-iterating throws");
        Stream<Integer> once = nums.stream();
        once.forEach(n -> System.out.print(n + " "));
        System.out.println();
        try {
            once.count();
        } catch (IllegalStateException e) {
            System.out.println("re-use: " + e.getMessage());
        }

        section("3) Collection is REUSABLE - iterate as many times as you like");
        for (int pass = 1; pass <= 2; pass++) {
            System.out.print("pass " + pass + ": ");
            for (int n : nums) System.out.print(n + " ");
            System.out.println();
        }

        section("4) External vs Internal iteration");
        // External - caller drives the cursor
        Iterator<Integer> it = nums.iterator();
        System.out.print("external: ");
        while (it.hasNext()) System.out.print(it.next() + " ");
        System.out.println();

        // Internal - stream drives
        System.out.print("internal: ");
        nums.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("5) Stream does NOT mutate the source");
        List<Integer> src = new ArrayList<>(List.of(1, 2, 3));
        List<Integer> doubled = src.stream().map(n -> n * 2).collect(Collectors.toList());
        System.out.println("src     = " + src);     // unchanged
        System.out.println("doubled = " + doubled);

        section("6) Mutating the source DURING iteration throws CME");
        final List<Integer> bag = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        try {
            bag.stream().forEach(n -> { if (n == 3) bag.remove(Integer.valueOf(3)); });
        } catch (java.util.ConcurrentModificationException cme) {
            System.out.println("caught ConcurrentModificationException - don't mutate source mid-stream");
        }
        // The safe way: collect() into a NEW list.
        List<Integer> safeBag = bag.stream().filter(n -> n != 3).collect(Collectors.toList());
        System.out.println("after safe remove = " + safeBag);

        section("7) Size - Collection.size vs Stream.count");
        System.out.println("nums.size()        = " + nums.size());
        System.out.println("stream().count()    = " + nums.stream().count());

        section("8) Crossing the boundary back and forth");
        // Collection -> Stream
        Stream<Integer> s = nums.stream();
        // Stream -> Collection
        List<Integer> filtered = s.filter(n -> n > 3).collect(Collectors.toList());
        System.out.println("filtered = " + filtered);

        section("9) Picking the right tool");
        System.out.println("""
                Use a Collection when:
                  - You need to STORE data that the rest of the code will use.
                  - You need to read the data multiple times.
                  - You need positional / keyed access.

                Use a Stream when:
                  - You want to COMPUTE a result via filter / map / reduce.
                  - You want declarative, composable, parallelisable code.
                  - You only need ONE pass over the data.
                """);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
