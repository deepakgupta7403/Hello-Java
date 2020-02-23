package Basics.Variables;

/**Instance variables are non-static variables and are declared in a class outside any method, constructor or block.
 * As instance variables are declared in a class, these variables are created when an object of the class is created and destroyed when the object is destroyed.
 * Unlike local variables, we may use access specifiers for instance variables. If we do not specify any access specifier then the default access specifier will be used.
 * Initialisation of Instance Variable is not Mandatory. Its default value is 0.
 * Instance Variable can be accessed only by creating objects.**/

public class InstanceVariableExample {
    // These variables are instance variables.
    // These variables are in a class
    // and are not inside any function
    int engMarks;
    int mathsMarks;
    int phyMarks;

    public static void main(String[] args) {
        // first object
        InstanceVariableExample obj1 = new InstanceVariableExample();
        obj1.engMarks = 50;
        obj1.mathsMarks = 80;
        obj1.phyMarks = 90;

        // second object
        InstanceVariableExample obj2 = new InstanceVariableExample();
        obj2.engMarks = 80;
        obj2.mathsMarks = 60;
        obj2.phyMarks = 85;

        // displaying marks for first object
        System.out.println("Marks for first object:");
        System.out.println(obj1.engMarks);
        System.out.println(obj1.mathsMarks);
        System.out.println(obj1.phyMarks);

        // displaying marks for second object
        System.out.println("Marks for second object:");
        System.out.println(obj2.engMarks);
        System.out.println(obj2.mathsMarks);
        System.out.println(obj2.phyMarks);
    }

    // OUTPUT
    // Marks for first object:
    //50
    //80
    //90
    //Marks for second object:
    //80
    //60
    //85
}
