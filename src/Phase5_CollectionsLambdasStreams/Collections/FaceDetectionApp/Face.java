package Phase5_CollectionsLambdasStreams.Collections.FaceDetectionApp;

/**
 * Face - the single domain record used throughout the Face Detection app.
 *
 * Implemented as a Java RECORD (Java 16+). This gives us:
 *   - immutable value semantics suitable for use as a Map key,
 *   - equals/hashCode generated from all components,
 *   - a Comparable natural order (by confidence) we define ourselves below.
 *
 * Fields
 * ------
 *   id          - unique identifier of the detection event
 *   personName  - matched person, or "unknown"
 *   confidence  - 0.0..1.0 confidence of the match
 *   timestampMs - when the face was detected (epoch ms)
 *   x, y, w, h  - bounding box in the source image
 *
 * Natural ordering = descending confidence (so a sorted collection
 * surfaces the most-confident match first).
 */
public record Face(
        int    id,
        String personName,
        double confidence,
        long   timestampMs,
        int    x, int y, int w, int h
) implements Comparable<Face> {

    public Face {
        if (personName == null || personName.isBlank()) {
            throw new IllegalArgumentException("personName required");
        }
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be in [0,1]");
        }
    }

    /** Higher confidence comes first - good fit for a max-heap PriorityQueue. */
    @Override
    public int compareTo(Face other) {
        return Double.compare(other.confidence, this.confidence);
    }

    public boolean isUnknown() {
        return "unknown".equalsIgnoreCase(personName);
    }
}
