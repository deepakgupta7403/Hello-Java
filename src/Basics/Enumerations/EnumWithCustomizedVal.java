package Basics.Enumerations;

/**By default enums have their own string values, we can also assign some custom values to enums.
 * Consider below example for that.
 *
 * enum  Fruits
 * {
 *     APPLE(“RED”), BANANA(“YELLOW”), GRAPES(“GREEN”);
 * }
 *
 *
 *In above example we can see that the Fruits enum have three members i.e APPLE, BANANA and GRAPES with have their own
 * different custom values RED, YELLOW and GREEN respectively.**/


// Java program to demonstrate how values can be assigned to enums.
enum FRUITS{

    // This will call enum constructor with one String argument
    APPLE("RED"),
    BANANA("YELLOW"),
    GREPS("GREEN");

    // declaring private variable for getting values
    String color;

    //getter method
    public String getColor() {
        return color;
    }

    // enum constructor - cannot be public or protected
    FRUITS(String color) {
        this.color = color;
    }

}

public class EnumWithCustomizedVal {
    public static void main(String[] args) {
        FRUITS[] fruits = FRUITS.values();

        for (FRUITS fruits1 : fruits) {
            System.out.println(fruits1 +" Color is "+ fruits1.getColor());
        }
    }


    // OUTPUT
    //APPLE Color is RED
    //BANANA Color is YELLOW
    //GREPS Color is GREEN
}
