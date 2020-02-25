package Basics.Extra;

/**
 * Java 9 has made changes in features of java language and eliminating underscore from legal name is a major change made by Oracle.
 * The use of the variable name _ in any context is never encouraged.
 * Latest versions of the Java reserve this name as a keyword and/or give it special semantics.
 * If you use the underscore character (“_”) an identifier, your source code can no longer be compiled. You will get compile time error.
 *
 *
 * Important points:
 *
 * 1) Using underscore in a variable like first_name is still valid. But using _ alone as variable name is no more valid.
 * 2) Even if you are using earlier versions of Java, using only underscore as variable name is just plain bad style of programming and must be avoided.
 * **/

public class _UnderscoreInJava {
    public static void main(String[] args) {

    }

    private void jdk8() {
        int _ = 10;
        System.out.println(_);

        //OUTPUT
        //10
    }

    //In Java 9, underscore as variable name won’t work altogether. Below source code can no longer be compiled.

    private void jdk9() {

        // Java program to illustrate
        // using underscore as
        // variable name in java 9
        int _ = 10;
        System.out.println(_);

        //OUTPUT
        //10
    }
}
