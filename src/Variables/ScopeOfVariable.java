package Variables;

/**These variables must be declared inside class (outside any function).
 * They can be directly accessed anywhere in class. Let’s take a look at an example:
 *
 * We can declare class variables anywhere in class, but outside methods.
 * Access specified of member variables doesn’t effect scope of them within a class.
 * Member variables can be accessed outside a class with following rules
 *
 * Modifier      Package  Subclass  World
 *
 * public          Yes      Yes     Yes
 *
 * protected       Yes      Yes     No
 *
 * Default (no
 * modifier)       Yes       No     No
 *
 * private         No        No     No
 *
 * **/
class GlobalVarible{
    // All variables defined directly inside a class
    // are member variables
    int a;
    private String b;
    void method1() {}
    int method2() {return 0;}
    char c;
}











/**Variables declared inside a method have method level scope and can’t be accessed outside the method.
 * this type of variable we can't use outside of the method**/

class LocalVariable {
    void method1()
    {
        // Local variable (Method level scope)
        int x;
    }

/**
 * Local variables don’t exist after method’s execution is over.
 * Here’s another example of method scope, except this time the variable got passed in as a parameter to the method
 * The below code uses this keyword to differentiate between the local and class variables.**/
    private int x;
    public void setX(int x)
    {
        this.x = x;
    }
}

class LocalExample
{
    static int x = 11;
    private int y = 33;
    public void method1(int x)
    {
        LocalExample t = new LocalExample();
        this.x = 22;
        y = 44;

        System.out.println("Test.x: " + LocalExample.x);
        System.out.println("t.x: " + t.x);
        System.out.println("t.y: " + t.y);
        System.out.println("y: " + y);
    }

    public static void main(String args[])
    {
        LocalExample t = new LocalExample();
        t.method1(5);
    }


    //OUTPUT
    //Test.x: 22
    //t.x: 22
    //t.y: 33
    //y: 44
}


/**
 * Loop Variables (Block Scope)
 * A variable declared inside pair of brackets “{” and “}” in a method has scope withing the brackets only.
 **/

class LoopVariable {
    public static void main(String args[]) {
        for (int x = 0; x < 4; x++) {
            System.out.println(x);
        }

        // Will produce error
        // Uncomment below line and execute the program
        // System.out.println(x);
    }

    //OUTPUT
    //error: cannot find symbol
    //        System.out.println(x);

    public void RightProgram() {
        int x;
        for (x = 0; x < 5; x++) {
            System.out.println(x);
        }
        System.out.println(x);

        //Call This Method, and the OUT is like...
        //0
        //1
        //2
        //3
        //4
        //5
    }


    //Error One
    public void ErrorOne() {
        int a = 5;
        //uncomment and run the program
//        for (int a = 0; a < 5; a++)
//        {
//            System.out.println(a);
//        }

        //Error Message is
        //error: variable a is already defined in method go(int)
        //       for (int a = 0; a < 5; a++)
        //                ^
        //1 error
    }
}



