package Phase5_CollectionsLambdasStreams.Collections.FaceDetectionApp;

import java.util.List;
import java.util.Map;

/**
 * FaceDetectionApp - runnable demo that ties everything together.
 * <p>
 *
 * The program SIMULATES 20 video frames from a Detector. Each frame is fed
 * into a FaceRepository which exercises every collection type. Then we
 * print a series of reports that exercise the queries those data structures
 * make cheap.
 * <p>
 *
 * Run:
 *      cd src
 *      javac Basics/Collections/FaceDetectionApp/*.java
 *      java  Basics.Collections.FaceDetectionApp.FaceDetectionApp
 * <p>
 *
 * Collection cheat-sheet for this demo
 * ------------------------------------
 *   ArrayList&lt;Face&gt;         all detections in order
 *   ArrayDeque&lt;List&lt;Face&gt;&gt; sliding window of recent frames
 *   LinkedHashSet&lt;String&gt;   distinct people, insertion order
 *   HashSet&lt;Integer&gt;        face IDs the user dismissed (O(1) check)
 *   HashMap&lt;String,List&gt;    faces grouped by person
 *   HashMap&lt;String,Long&gt;    per-person counter via Map.merge
 *   TreeMap&lt;Long,Face&gt;      timeline for range queries
 *   PriorityQueue&lt;Face&gt;     alerts queue, max-confidence first
 */
public class FaceDetectionApp {

    public static void main(String[] args) {

        Detector       det  = new Detector(42L);          // seed for determinism
        FaceRepository repo = new FaceRepository();

        // ===== Simulate 20 frames =====
        for (int frame = 0; frame < 20; frame++) {
            List<Face> faces = det.detect(frame);
            repo.recordFrame(frame, faces);
        }

        // User dismisses some random face IDs (suppose UI clicks).
        repo.dismiss(3);
        repo.dismiss(7);

        // ===== Reports =====

        section("1) Summary");
        System.out.println("Total detections   : " + repo.totalDetected());
        System.out.println("Distinct people    : " + repo.distinctPeople());
        System.out.println("Dismissed IDs      : " + repo.dismissedCount());
        System.out.println("Frames in window   : " + repo.windowSize());

        section("2) People seen (insertion order)");
        for (String name : repo.peopleSeen()) {
            System.out.println("  " + name + "  count=" + repo.counts().get(name));
        }

        section("3) Top-3 most confident detections (PriorityQueue trick)");
        for (Face f : repo.topK(3)) {
            System.out.printf("  id=%d  %-9s conf=%.2f%n", f.id(), f.personName(), f.confidence());
        }

        section("4) Recent people in the sliding window (last few frames)");
        System.out.println("  " + repo.recentPeople());

        section("5) Timeline range query - earliest 100ms");
        long earliest = repo.all().isEmpty() ? 0L : repo.all().get(0).timestampMs();
        var window = repo.inRange(earliest, earliest + 100);
        System.out.println("  " + window.size() + " face(s) in [" + earliest + ", " + (earliest + 100) + "]");
        for (Face f : window) {
            System.out.printf("    ts=%d id=%d %s conf=%.2f%n",
                    f.timestampMs(), f.id(), f.personName(), f.confidence());
        }

        section("6) Alerts queue (high-confidence, non-dismissed, known person)");
        Face alert;
        while ((alert = repo.nextAlert()) != null) {
            System.out.printf("  ALERT id=%d  %-9s conf=%.2f%n",
                    alert.id(), alert.personName(), alert.confidence());
        }

        section("7) Index by person - first 3 faces for each");
        for (Map.Entry<String, List<Face>> e : repo.byPerson().entrySet()) {
            String  name  = e.getKey();
            List<Face> fs = e.getValue();
            System.out.println("  " + name + "  total=" + fs.size());
            fs.stream().limit(3).forEach(f ->
                System.out.printf("      id=%d conf=%.2f ts=%d%n",
                        f.id(), f.confidence(), f.timestampMs())
            );
        }

        // SAMPLE OUTPUT (numbers depend on the seeded Random)
        // ====== 1) Summary ======
        // Total detections   : 30
        // Distinct people    : 6
        // Dismissed IDs      : 2
        // Frames in window   : 5
        // ====== 2) People seen (insertion order) ======
        //   Alice  count=6
        //   Carol  count=5
        //   unknown count=4
        //   ...
        // ====== 3) Top-3 most confident detections (PriorityQueue trick) ======
        //   id=12 Alice    conf=0.99
        //   id=27 Dave     conf=0.97
        //   id= 5 Bob      conf=0.95
        // ...
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
