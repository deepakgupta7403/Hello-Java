package Basics.Collections.FaceDetectionApp;

import java.util.List;
import java.util.Random;

/**
 * Detector - a fake "ML model" that turns a Frame number into a list of
 * detected Faces. In a real system this would hand the image to OpenCV or
 * an ML model; here we generate deterministic-but-varied data so the demo
 * is reproducible.
 *
 * The class exposes a single method, detect(int frameNo), which returns a
 * fresh List of Face records.
 *
 * NOTE: We use a seeded Random so multiple runs produce the same output.
 */
public class Detector {

    private static final String[] PEOPLE = {
            "Alice", "Bob", "Carol", "Dave", "Eve", "unknown"
    };

    private final Random rng;
    private int nextId = 1;

    public Detector(long seed) {
        this.rng = new Random(seed);
    }

    /** Returns 0..3 detected faces for a given frame number. */
    public List<Face> detect(int frameNo) {
        int n = rng.nextInt(4);                         // 0..3 detections
        long ts = 1_700_000_000_000L + frameNo * 33L;   // ~30 fps
        List<Face> out = new java.util.ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String name      = PEOPLE[rng.nextInt(PEOPLE.length)];
            double confidence = 0.40 + rng.nextDouble() * 0.60;     // 0.40..1.00
            int x = rng.nextInt(800), y = rng.nextInt(600);
            int w = 40 + rng.nextInt(80), h = 40 + rng.nextInt(80);
            out.add(new Face(nextId++, name, confidence, ts, x, y, w, h));
        }
        return out;
    }
}
