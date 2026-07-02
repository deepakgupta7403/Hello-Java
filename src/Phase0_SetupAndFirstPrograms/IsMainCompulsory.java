package Phase0_SetupAndFirstPrograms;

/**
 * <h1>Is a <code>main</code> Method Compulsory?</h1>
 *
 * <p>The answer to this question depends on the version of Java you are using. Prior to
 * JDK 7, the <code>main</code> method was not mandatory in a Java program. You could write
 * your full code under a <code>static</code> block and it ran normally.</p>
 *
 * <p>The <code>static</code> block is first executed as soon as the class is loaded, before
 * the <code>main()</code> method is invoked and therefore before <code>main()</code> is
 * called. <code>main</code> is usually declared as a <code>static</code> method, and hence
 * Java doesn’t need an object to call the <code>main</code> method.</p>
 *
 * <p>When you give the run command (i.e. <code>java Test</code> in the below-mentioned program
 * in Notepad), the compiler presumes <code>Test</code> is the class in which <code>main()</code>
 * resides, and since the compiler loads the <code>main()</code> method, static blocks are ready
 * to get executed. So here, it will run the <code>static</code> block first and then it will see
 * that no <code>main()</code> is there. Therefore it will give an “exception”, as the exception
 * comes during execution. However, if we don’t want an exception, we can terminate the program
 * with <code>System.exit(0)</code>.</p>
 *
 * @author  Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
 */

public class IsMainCompulsory {

    // This program will successfully run
    // prior to JDK 7
    /*public class Test
    {
        // static block
        static
        {
            System.out.println("Hello User");
        }
    }*/
}
