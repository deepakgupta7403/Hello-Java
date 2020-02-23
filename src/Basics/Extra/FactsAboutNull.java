package Basics.Extra;

/**
 * Almost all the programming languages are bonded with null. There is hardly a programmer, who is not troubled by null.
 * In Java, null is associated java.lang.NullPointerException. As it is a class in java.lang package, it is called when we try to perform some operations with or without null and sometimes we don’t even know where it has happened.
 * Below are some important points about null in java which every Java programmer should know:
 **/
public class FactsAboutNull {

    public static void main(String[] args) {

    }

    //null is CaseSensitive:

    /**
     * null is literal in Java and because keywords are case-sensitive in java, we can’t write NULL or 0 as in C language.
     **/
    private void nullCaseSensitive() {
        // compile-time error : can't find symbol 'NULL'
        Object obj = 0/*NULL*/;

        //runs successfully
        Object obj1 = null;

        //OUTPUT
        /*5: error: cannot find symbol
        can't find symbol 'NULL'
                ^
                variable NULL*/
    }


    // Reference Variable value
    /**
     * Any reference variable in Java has default value null.
     **/
    private static Object obj;

    private void referenceVariable() {
        // it will print null;
        System.out.println("Value of object obj is : " + obj);

        //OUTPUT
        //Value of object obj is : null
    }


    //Types of null
    /**
     * Unlike common misconception, null is not Object or neither a type.
     * It’s just a special value, which can be assigned to any reference type and you can type cast null to any type
     **/

    // null can be assigned to String
    String str = null;

    // you can assign null to Integer also
    Integer itr = null;

    // null can also be assigned to Double
    Double dbl = null;

    // null can be type cast to String
    String myStr = (String) null;

    // it can also be type casted to Integer
    Integer myItr = (Integer) null;

    // yes it's possible, no error
    Double myDbl = (Double) null;


    //Autoboxing and unboxing
    private void boxingUnbox() throws java.lang.NullPointerException {
        //An integer can be null, so this is fine
        Integer i = null;

        //Unboxing null to integer throws NullpointerException
        int a = i;


        //OUTPUT
        //Exception in thread "main" java.lang.NullPointerException
        //    at Test.main(Test.java:6)
    }


    //instanceof operator
    /**The java instanceof operator is used to test whether the object is an instance of the specified type (class or subclass or interface).
     * At run time, the result of the instanceof operator is true if the value of the Expression is not null.
     * This is an important property of instanceof operation which makes it useful for type casting checks.
     **/
    private void instanceOfExample() throws java.lang.Exception {
        Integer i = null;
        Integer j = 10;

        //prints false
        System.out.println(i instanceof Integer);

        //Compiles successfully
        System.out.println(j instanceof Integer);

        //OUTPUT
        //false
        //true
    }


    //Static and Non-Static Methods

    /**
     * We cannot call a non-static method on a reference variable with null value, it will throw NullPointerException, but we can call static method with reference variables with null values.
     * Since static methods are bonded using static binding, they won’t throw Null pointer Exception.
     **/
    private void staticNonStaticMethod() {
        FactsAboutNull obj = null;
        obj.nonStaticMethod();
        staticMethod();

        //OUTPUT
        //static method, can be called by null referenceException in thread "main"
        //java.lang.NullPointerException
        //    at Test.main(Test.java:5)
    }

    private static void staticMethod() {
        //Can be called by null reference
        System.out.println("static method, can be called by null reference");

    }

    private void nonStaticMethod() {
        //Can not be called by null reference
        System.out.print(" Non-static method- ");
        System.out.println("cannot be called by null reference");

    }



    //== and != Operator
    /**The comparision and not equal to operators are allowed with null in Java.
     * This can made useful in checking of null with objects in java.**/
    private void checkNullNotNull() {
        //return true;
        System.out.println(null==null);

        //return false;
        System.out.println(null!=null);

        //OUTPUT
        //true
        //false
    }
}
