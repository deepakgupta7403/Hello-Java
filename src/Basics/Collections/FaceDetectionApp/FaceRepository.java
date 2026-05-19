package Basics.Collections.FaceDetectionApp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

/**
 * FaceRepository - the heart of the demo. ONE class that uses every
 * collection type from the framework:
 *
 *   List&lt;Face&gt;                    - history of all detections (ArrayList)
 *   Deque&lt;List&lt;Face&gt;&gt;             - sliding window of recent FRAMES (ArrayDeque)
 *   Set&lt;String&gt;                   - distinct people seen so far (LinkedHashSet)
 *   HashSet&lt;Integer&gt;              - face IDs the user has DISMISSED
 *   Map&lt;String, List&lt;Face&gt;&gt;       - faces indexed by person (HashMap)
 *   Map&lt;String, Long&gt;             - per-person detection counter (HashMap)
 *   NavigableMap&lt;Long, Face&gt;      - timeline by timestamp (TreeMap)
 *   PriorityQueue&lt;Face&gt;           - alerts queue ranked by confidence
 *
 * Each collection is justified by what the OPERATION needs. That is the
 * design lesson: choose the data structure that makes the dominant query
 * cheap.
 */
public class FaceRepository {

    // History of ALL detected faces, in detection order. ArrayList because:
    //   - we mostly append at the end,
    //   - we sometimes scan to compute statistics.
    private final List<Face> all = new ArrayList<>();

    // The last N frames as a sliding window. ArrayDeque because we need to
    // add at one end and remove from the other in O(1).
    private final Deque<List<Face>> recentFrames = new ArrayDeque<>();
    private static final int WINDOW = 5;

    // Distinct people in first-seen order. LinkedHashSet because:
    //   - we want uniqueness (Set),
    //   - and we want iteration in insertion order for human readability.
    private final Set<String> peopleSeen = new LinkedHashSet<>();

    // Face IDs that the user clicked "dismiss" on. HashSet for O(1) lookup;
    // order doesn't matter.
    private final Set<Integer> dismissed = new HashSet<>();

    // Faces grouped by person. HashMap because:
    //   - we don't need an ordered view of people,
    //   - we look up by name in O(1).
    private final Map<String, List<Face>> facesByPerson = new HashMap<>();

    // How many times each person has been seen. Same justification as above.
    private final Map<String, Long> detectionCount = new HashMap<>();

    // All faces by timestamp. TreeMap because we frequently ask range
    // queries ("everything in the last 5 minutes") that need O(log n)
    // navigation.
    private final NavigableMap<Long, Face> timeline = new TreeMap<>();

    // High-confidence "alerts" pending notification. PriorityQueue because
    // we want to pop the most confident match next, regardless of insertion
    // order.
    private final PriorityQueue<Face> alerts = new PriorityQueue<>();   // Face.compareTo is desc by conf

    /** Push every face from one detected frame through all the data structures. */
    public void recordFrame(int frameNo, List<Face> faces) {
        // Update sliding window
        recentFrames.offerLast(new ArrayList<>(faces));
        while (recentFrames.size() > WINDOW) {
            recentFrames.pollFirst();
        }

        for (Face f : faces) {
            all.add(f);
            peopleSeen.add(f.personName());
            facesByPerson.computeIfAbsent(f.personName(), k -> new ArrayList<>()).add(f);
            detectionCount.merge(f.personName(), 1L, Long::sum);
            timeline.put(f.timestampMs(), f);
            if (f.confidence() >= 0.85 && !dismissed.contains(f.id()) && !f.isUnknown()) {
                alerts.offer(f);
            }
        }
    }

    public void dismiss(int faceId) {
        dismissed.add(faceId);
        // Also lazily drop any pending alert with that id.
        alerts.removeIf(f -> f.id() == faceId);
    }

    // ===== reads =====

    public List<Face> all()                    { return Collections.unmodifiableList(all); }
    public Set<String> peopleSeen()            { return Collections.unmodifiableSet(peopleSeen); }
    public Map<String, Long> counts()          { return Collections.unmodifiableMap(detectionCount); }
    public Map<String, List<Face>> byPerson()  { return Collections.unmodifiableMap(facesByPerson); }

    /** Pop the next alert (most-confident first). Returns null if empty. */
    public Face nextAlert() {
        return alerts.poll();
    }

    /** Faces detected between [fromMs, toMs] inclusive - range query on the TreeMap. */
    public Collection<Face> inRange(long fromMs, long toMs) {
        return timeline.subMap(fromMs, true, toMs, true).values();
    }

    /** Top-K faces by confidence across all of history. */
    public List<Face> topK(int k) {
        // Bounded MIN-heap: kick out the smallest when we exceed K. Drain at the end.
        PriorityQueue<Face> minByConf = new PriorityQueue<>(
                Comparator.comparingDouble(Face::confidence)
        );
        for (Face f : all) {
            if (minByConf.size() < k) {
                minByConf.offer(f);
            } else if (f.confidence() > minByConf.peek().confidence()) {
                minByConf.poll();
                minByConf.offer(f);
            }
        }
        List<Face> result = new ArrayList<>(minByConf);
        result.sort(Comparator.comparingDouble(Face::confidence).reversed());
        return result;
    }

    /** Distinct people in the last `windowSize` frames. */
    public Set<String> recentPeople() {
        Set<String> recent = new LinkedHashSet<>();
        for (List<Face> frame : recentFrames) {
            for (Face f : frame) {
                if (!f.isUnknown()) recent.add(f.personName());
            }
        }
        return recent;
    }

    public int totalDetected()    { return all.size(); }
    public int distinctPeople()   { return peopleSeen.size(); }
    public int dismissedCount()   { return dismissed.size(); }
    public int pendingAlerts()    { return alerts.size(); }
    public int windowSize()       { return recentFrames.size(); }
}
