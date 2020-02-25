package Basics.Enumerations;

/**
 * enum can contain constructor and it is executed separately for each enum constant at the time of enum class loading.
 * We can’t create enum objects explicitly and hence we can’t invoke enum constructor directly.
 * enum and methods :
 * <p>
 * enum can contain concrete methods only i.e. no any abstract method.
 * filter_none
 **/


// Java program to demonstrate that enums can have constructor
// and concrete methods.

// An enum (Note enum keyword inplace of class keyword)
enum Color {
    RED, GREEN, BLUE;

    // enum constructor called separately for each constant
    private Color() {
        System.out.println("Constructor called for : " +
                this.toString());
    }

    // Only concrete (not abstract) methods allowed
    public void colorInfo() {
        System.out.println("Universal Color");
    }
}

public class EnumConstructor {

    public static void main(String[] args) {
        Color c1 = Color.RED;
        System.out.println(c1);
        c1.colorInfo();
    }

    //OUTPUT
    //Constructor called for : RED
    //Constructor called for : GREEN
    //Constructor called for : BLUE
    //RED
    //Universal Color
}
