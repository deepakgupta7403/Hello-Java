package Phase3_ObjectOrientation.Constructors;

/**
 * Constructors in Java
 * --------------------
 * A CONSTRUCTOR is a special method that runs automatically when a new object
 * is created with `new`. Its job is to put the object into a valid initial
 * state: assign fields, allocate resources, register the object somewhere.
 *
 *
 * Constructor Rules
 * -----------------
 *  - Same NAME as the class.
 *  - NO return type (not even void).
 *  - May have any access modifier (public/protected/package/private).
 *  - Called via `new ClassName(...)`.
 *  - If you define NO constructor, the compiler synthesises a public no-arg
 *    "default constructor" that just calls super().
 *  - If you define ANY constructor, the default constructor is NOT generated.
 *  - The very first statement in a constructor body is implicitly
 *    `super();` unless you wrote `this(...)` or `super(...)` explicitly.
 *  - Constructors are NOT inherited (subclasses must define their own and
 *    call super(...) if needed).
 *
 *
 * Types Demonstrated Here
 * -----------------------
 *  1. DEFAULT constructor          - no parameters
 *  2. PARAMETERIZED constructor    - takes initial field values
 *  3. CONSTRUCTOR OVERLOADING      - several constructors with different sigs
 *  4. CONSTRUCTOR CHAINING         - this(...) and super(...)
 *  5. COPY constructor             - "deep" or "shallow" copy of another object
 *  6. PRIVATE constructor          - blocks `new` from outside (singletons,
 *                                    utility classes, builders)
 *
 *
 * Common Errors / Pitfalls
 * ------------------------
 *  - Recursive `this(...)` chain -> compile error "recursive constructor call".
 *  - Throwing from a super-class constructor is fine; partially constructed
 *    instances are unsafe to publish.
 *  - Calling overridable methods inside a constructor is risky: subclasses
 *    may see un-initialised state.
 *  - Constructors do NOT have `return` types - even `void` is an error.
 */

public class Constructors {

    // ============================================================
    // Demo class with many constructor styles
    // ============================================================
    static class Book {
        private final String title;
        private final String author;
        private final int    pages;

        // 1) DEFAULT - no-arg constructor. If we deleted ALL constructors,
        //    the compiler would generate this automatically.
        public Book() {
            this("Unknown", "Anonymous", 0);    // chain to the 3-arg version
            System.out.println("default Book() called");
        }

        // 2) PARAMETERIZED - takes title + author with a sensible default pages
        public Book(String title, String author) {
            this(title, author, 0);             // chain to the 3-arg version
        }

        // 3) Full PARAMETERIZED - all fields explicitly set
        public Book(String title, String author, int pages) {
            if (title == null || author == null) {
                throw new IllegalArgumentException("title/author must not be null");
            }
            if (pages < 0) {
                throw new IllegalArgumentException("pages must be >= 0");
            }
            this.title  = title;
            this.author = author;
            this.pages  = pages;
        }

        // 4) COPY CONSTRUCTOR - clone an existing Book
        public Book(Book other) {
            this(other.title, other.author, other.pages);
        }

        @Override public String toString() {
            return "Book{" + title + " by " + author + ", " + pages + " pages}";
        }
    }

    // ============================================================
    // Inheritance + super(...) chaining
    // ============================================================
    static class Person {
        protected final String name;

        public Person(String name) {
            if (name == null) throw new IllegalArgumentException("name");
            this.name = name;
            System.out.println("Person(\"" + name + "\") constructor running");
        }
    }

    static class Employee extends Person {
        private final double salary;

        public Employee(String name, double salary) {
            super(name);            // MUST be the first statement
            this.salary = salary;
            System.out.println("Employee(\"" + name + "\", " + salary + ") constructor running");
        }

        @Override public String toString() {
            return "Employee{name=" + name + ", salary=" + salary + "}";
        }
    }

    // ============================================================
    // Private constructor - utility / singleton patterns
    // ============================================================
    static final class MathUtil {
        private MathUtil() {                    // prevents `new MathUtil()`
            throw new UnsupportedOperationException("utility class");
        }
        public static int square(int n) { return n * n; }
    }

    static final class Config {
        private static final Config INSTANCE = new Config();
        private int retries = 3;

        private Config() { }                    // singleton
        public static Config getInstance() { return INSTANCE; }
        public int retries() { return retries; }
    }

    public static void main(String[] args) {

        section("1) Default + parameterised + chaining");
        Book b1 = new Book();                                     // default -> 3-arg
        Book b2 = new Book("Effective Java", "Joshua Bloch");     // 2-arg -> 3-arg
        Book b3 = new Book("Clean Code", "Robert Martin", 464);   // 3-arg directly
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);

        section("2) Validation inside a constructor");
        try {
            new Book(null, "X", 10);
        } catch (IllegalArgumentException e) {
            System.out.println("caught: " + e.getMessage());
        }

        section("3) Copy constructor");
        Book copy = new Book(b3);
        System.out.println("original  = " + b3);
        System.out.println("copy      = " + copy);
        System.out.println("same ref? = " + (b3 == copy));    // false

        section("4) Inheritance + super(...)");
        Employee e = new Employee("Deepak", 55000);
        System.out.println(e);

        section("5) Private constructor - utility class");
        System.out.println("MathUtil.square(5) = " + MathUtil.square(5));
        try {
            // Even reflection cannot bypass without setAccessible; the throw
            // inside the constructor is an extra belt-and-braces guard.
            java.lang.reflect.Constructor<MathUtil> c =
                    MathUtil.class.getDeclaredConstructor();
            c.setAccessible(true);
            c.newInstance();
        } catch (Exception ex) {
            System.out.println("reflection blocked: " + ex.getCause().getMessage());
        }

        section("6) Private constructor - singleton");
        Config cfg = Config.getInstance();
        System.out.println("retries = " + cfg.retries());
        System.out.println("same instance? " + (cfg == Config.getInstance()));

        // OUTPUT
        // ====== 1) Default + parameterised + chaining ======
        // default Book() called
        // Book{Unknown by Anonymous, 0 pages}
        // Book{Effective Java by Joshua Bloch, 0 pages}
        // Book{Clean Code by Robert Martin, 464 pages}
        // ====== 2) Validation inside a constructor ======
        // caught: title/author must not be null
        // ====== 3) Copy constructor ======
        // original  = Book{Clean Code by Robert Martin, 464 pages}
        // copy      = Book{Clean Code by Robert Martin, 464 pages}
        // same ref? = false
        // ====== 4) Inheritance + super(...) ======
        // Person("Deepak") constructor running
        // Employee("Deepak", 55000.0) constructor running
        // Employee{name=Deepak, salary=55000.0}
        // ====== 5) Private constructor - utility class ======
        // MathUtil.square(5) = 25
        // reflection blocked: utility class
        // ====== 6) Private constructor - singleton ======
        // retries = 3
        // same instance? true
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
