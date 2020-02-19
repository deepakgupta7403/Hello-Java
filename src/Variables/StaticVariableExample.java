package Variables;

/**Static variables are also known as Class variables.
 * These variables are declared similarly as instance variables, the difference is that static variables are declared using the static keyword within a class outside any method constructor or block.
 * Unlike instance variables, we can only have one copy of a static variable per class irrespective of how many objects we create.
 * Static variables are created at the start of program execution and destroyed automatically when execution ends.
 * Initialisation of Static Variable is not Mandatory. Its default value is 0.
 * If we access the static variable like Instance variable (through an object), the compiler will show the warning message and it won’t halt the program. The compiler will replace the object name to class name automatically.
 * If we access the static variable without the class name, Compiler will automatically append the class name.
 *
 * To access static variables, we need not create an object of that class, we can simply access the variable as
 * class_name.variable_name;
 * **/
public class StaticVariableExample {
    // static variable salary
    public static double salary;
    public static String name = "Harsh";

    public static void main(String[] args) {

        // accessing static variable without object
        StaticVariableExample.salary = 100;

        System.out.println(StaticVariableExample.name +" salary is "+salary);

    }


    //    OUTPUT
    //    Harsh salary is 100.0
}
