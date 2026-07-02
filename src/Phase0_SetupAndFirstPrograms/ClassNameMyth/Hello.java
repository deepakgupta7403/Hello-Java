package Phase0_SetupAndFirstPrograms.ClassNameMyth;

/**
 * <h1>The Class Name Myth</h1>
 *
 * <h2>The Final Conclusion</h2>
 * <p>Whether a Java source file compiles depends on the relationship between the
 * class name, the file name, and whether the class is declared <code>public</code>.
 * There are three cases to consider.</p>
 *
 * <ul>
 *   <li>If the class is <code>public</code> and the file name is the same as the
 *       class name, then it works fine.</li>
 *   <li>If the class is <code>public</code> and the file name is different from the
 *       class name, then it throws an error &mdash; the class name and file name
 *       must be the same.</li>
 *   <li>If the class is not <code>public</code> and the file name is different from
 *       the class name, then it still works fine.</li>
 * </ul>
 *
 * <p>The programs demonstrating each case are shown below.</p>
 *
 * @author Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
 */

/*public class Hello {
    public static void main(String[] args) {
        System.out.println("In This case Class Name and File name are Same.");

        // OUTPUT
        // In This case Class Name and File name are Same.
    }
}*/


/*public class Overview.ClassNameMyth.HelloWorld {
    public static void main(String[] args) {
        System.out.println("In This case Class Name and File name are different.");

        // OUTPUT
        // Error:(13, 8) java: class Overview.ClassNameMyth.HelloWorld is public, should be declared in a file named Overview.ClassNameMyth.HelloWorld.java
    }
}*/


class HelloWorld {
    public static void main(String[] args) {
        System.out.println("In This case Class Name and File name are different but the class is not public.");

        // OUTPUT
        // In This case Class Name and File name are Same but the class is not public.

    }
}
