package Phase4_ErrorsAndTypeSafety.Annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import Phase4_ErrorsAndTypeSafety.Annotations.CustomAnnotations.Audited;
import Phase4_ErrorsAndTypeSafety.Annotations.CustomAnnotations.Author;
import Phase4_ErrorsAndTypeSafety.Annotations.CustomAnnotations.Schedule;
import Phase4_ErrorsAndTypeSafety.Annotations.CustomAnnotations.Service;

/**
 * Reading Annotations at Runtime
 * ------------------------------
 * For runtime-retained annotations, the Reflection API gives you:
 *
 *      class.getAnnotation(MyAnnot.class)
 *      class.getAnnotationsByType(MyAnnot.class)   // repeatables
 *      class.isAnnotationPresent(MyAnnot.class)
 *      class.getAnnotations()                       // declared on class
 *      class.getDeclaredAnnotations()               // not inherited
 *
 *      method.getAnnotation(MyAnnot.class)
 *      method.getAnnotationsByType(MyAnnot.class)
 *      method.getParameterAnnotations()
 *
 * This is the API frameworks like Spring, JUnit, Jackson, and Hibernate
 * use to drive their behaviour.
 *
 *
 * Pattern: hand-rolled @Audited interceptor
 * -----------------------------------------
 *   - Walk the class's methods.
 *   - If a method has @Audited, wrap its invocation with logging.
 *   - This is the conceptual core of AOP libraries.
 */

public class RuntimeAnnotations {

    public static void main(String[] args) throws Exception {

        section("1) Class-level annotation");
        Author authorAnn = Service.class.getAnnotation(Author.class);
        System.out.println("class @Author = " + authorAnn.value());

        section("2) Method-level annotation");
        Method save = Service.class.getMethod("save", String.class);
        Audited a = save.getAnnotation(Audited.class);
        System.out.println("@Audited label    = " + a.label());
        System.out.println("@Audited priority = " + a.priority());

        section("3) Repeatable annotation");
        Method cron = Service.class.getMethod("cron");
        Schedule[] schedules = cron.getAnnotationsByType(Schedule.class);
        System.out.print("days: ");
        for (Schedule s : schedules) System.out.print(s.value() + " ");
        System.out.println();

        section("4) Dump every annotation on every method");
        for (Method m : Service.class.getDeclaredMethods()) {
            Annotation[] anns = m.getDeclaredAnnotations();
            if (anns.length == 0) continue;
            System.out.print("  " + m.getName() + " -> ");
            for (Annotation ann : anns) System.out.print(ann + " ");
            System.out.println();
        }

        section("5) Hand-rolled @Audited interceptor");
        invokeWithAuditing(new Service(), "save", "hello");

        section("done");
    }

    /**
     * Conceptually: a tiny AOP layer. Walks methods on an instance,
     * finds the one with the given name, checks for @Audited, and
     * logs around the call.
     */
    private static void invokeWithAuditing(Object target, String methodName, Object... args) throws Exception {
        Method m = null;
        for (Method candidate : target.getClass().getMethods()) {
            if (candidate.getName().equals(methodName) && candidate.getParameterCount() == args.length) {
                m = candidate;
                break;
            }
        }
        if (m == null) throw new NoSuchMethodException(methodName);

        Audited a = m.getAnnotation(Audited.class);
        if (a != null) System.out.println("[AUDIT pre]  " + a.label());
        long t0 = System.nanoTime();
        Object result = m.invoke(target, args);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        if (a != null) System.out.println("[AUDIT post] " + a.label() + " took " + ms + " ms");
        // (no return)
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
