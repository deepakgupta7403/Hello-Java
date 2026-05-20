package Phase3_ObjectOrientation.Interfaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Marker Interfaces
 * -----------------
 * A MARKER INTERFACE is an interface with NO METHODS - its sole purpose is to
 * "mark" a class as having some property. The JVM, frameworks, or your own
 * code can then check `instanceof MarkerName` and choose to treat marked
 * classes specially.
 *
 *      interface Serializable {}        // empty - just a tag
 *      class Customer implements Serializable {}
 *
 *      if (obj instanceof Serializable) {
 *          // safe to serialize
 *      }
 *
 *
 * Famous Built-In Markers
 * -----------------------
 *   java.io.Serializable   - "I can be written to an ObjectOutputStream".
 *   java.lang.Cloneable    - "I support Object.clone() - else CloneNotSupportedException".
 *   java.rmi.Remote        - "I can be invoked from a remote JVM via RMI".
 *   javax.ejb.EnterpriseBean (legacy) - tagged a class as an EJB.
 *
 * Each of these is empty; the SEMANTIC contract lives in the documentation
 * and is enforced by some external machinery (the JVM serialiser, the
 * `clone()` mechanism, the RMI stub generator).
 *
 *
 * Why Marker Interfaces Existed
 * -----------------------------
 * Before Java 5 introduced ANNOTATIONS, marker interfaces were the only way
 * to attach METADATA to a class that other code could inspect.
 *
 *
 * Markers vs Annotations
 * ----------------------
 *  Marker interface          | Annotation
 *  --------------------------+-----------------------------------------------
 *  Adds a TYPE (via          | Adds METADATA without changing the type
 *  `instanceof`)             | hierarchy.
 *  Can constrain a generic   | Cannot directly constrain a generic
 *  parameter:                | parameter (you would need a Class<?> + manual
 *      <T extends Marker>    | reflection check).
 *  Cannot carry data         | Can carry parameters: @Retention(RUNTIME)
 *  All-or-nothing            | Can be applied with options.
 *
 * MODERN GUIDANCE
 * ---------------
 *   - Pick an ANNOTATION for new "tag" metadata - more flexible, no API impact.
 *   - Use a MARKER INTERFACE when you genuinely need the marker to be part
 *     of the TYPE (e.g. for `instanceof` checks or as a generic upper bound).
 *   - The JDK keeps `Serializable` and `Cloneable` as interfaces partly for
 *     historical reasons - they predate annotations.
 *
 *
 * This file demonstrates:
 *   - using the built-in marker Serializable
 *   - what happens when you try to serialise an unmarked class
 *   - a custom marker interface you can write yourself
 *   - the modern equivalent done with an annotation
 */

public class MarkerInterface {

    // ============================================================
    // 1) Built-in marker - Serializable
    // ============================================================
    static class Marked implements Serializable {
        private static final long serialVersionUID = 1L;
        int value;
        Marked(int v) { this.value = v; }
        @Override public String toString() { return "Marked(" + value + ")"; }
    }

    static class Unmarked {
        int value;
        Unmarked(int v) { this.value = v; }
    }

    // ============================================================
    // 2) Custom marker interface
    // ============================================================
    /**
     * Marks a class as safe to AUDIT. An audit framework can then write
     * "any object implementing Auditable goes into the audit log".
     * The interface has no methods - it is a pure tag.
     */
    interface Auditable {}

    static class Order implements Auditable {
        String id; double amount;
        Order(String id, double amount) { this.id = id; this.amount = amount; }
        @Override public String toString() { return "Order(" + id + ", " + amount + ")"; }
    }

    static class CacheHit /* NOT auditable */ {
        String key;
        CacheHit(String key) { this.key = key; }
    }

    /** A pretend audit framework. Only audits objects that opt-in via the marker. */
    static void audit(Object o) {
        if (o instanceof Auditable) {
            System.out.println("AUDIT: " + o);
        } else {
            System.out.println("(skipped, not Auditable): " + o.getClass().getSimpleName());
        }
    }

    // ============================================================
    // 3) Modern equivalent - an annotation instead of a marker interface
    // ============================================================
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE)
    @interface AuditableV2 {}

    @AuditableV2
    static class Invoice {
        String num;
        Invoice(String n) { this.num = n; }
        @Override public String toString() { return "Invoice(" + num + ")"; }
    }

    static void auditV2(Object o) {
        if (o.getClass().isAnnotationPresent(AuditableV2.class)) {
            System.out.println("AUDIT v2: " + o);
        } else {
            System.out.println("(skipped v2): " + o.getClass().getSimpleName());
        }
    }

    public static void main(String[] args) throws Exception {

        section("1) Built-in marker - Serializable lets you serialise");
        Marked m = new Marked(42);
        byte[] bytes = serialize(m);
        Marked back = (Marked) deserialize(bytes);
        System.out.println("round-tripped: " + back);

        section("Unmarked class - serialisation REFUSED");
        Unmarked u = new Unmarked(99);
        try {
            serialize(u);
        } catch (NotSerializableException e) {
            System.out.println("caught NotSerializableException: " + e.getMessage());
        }

        section("2) Custom marker interface - Auditable");
        Object[] items = { new Order("O-1", 250.0), new CacheHit("user:42"), new Order("O-2", 50.0) };
        for (Object o : items) audit(o);

        section("3) Annotation as a modern marker");
        Object[] items2 = { new Invoice("INV-001"), new CacheHit("user:99") };
        for (Object o : items2) auditV2(o);

        section("4) Marker as a generic upper bound");
        // Only classes that implement Auditable can be passed into auditAll
        // because the generic type T is constrained to extend Auditable.
        auditAll(new Order("O-3", 12.50), new Order("O-4", 7.99));
        // auditAll(new CacheHit("x"));     // compile ERROR - not Auditable

        // OUTPUT
        // (matches the inline comments above)
    }

    /** A method usable only on Auditable types - markers as upper bounds. */
    @SafeVarargs
    private static <T extends Auditable> void auditAll(T... items) {
        for (T item : items) {
            System.out.println("AUDIT (generic): " + item);
        }
    }

    private static byte[] serialize(Object o) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream    oos  = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            return baos.toByteArray();
        }
    }

    private static Object deserialize(byte[] data) throws Exception {
        try (ByteArrayInputStream  bais = new ByteArrayInputStream(data);
             ObjectInputStream     ois  = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
