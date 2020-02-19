package ClassesAndObject;

/**The new operator instantiates a class by allocating memory for a new object and returning a reference to that memory.
 * The new operator also invokes the class constructor.
 *
 * This class contains a single constructor. We can recognize a constructor because its declaration uses the same name as the class and it has no return type.
 * The Java compiler differentiates the constructors based on the number and the type of the arguments. The constructor in the Dog class takes four arguments.
 * The following statement provides “tuffy”,”papillon”,5,”white” as values for those arguments:
 *
 * Dog tuffy = new Dog("tuffy","papillon",5, "white");**/

public class InitializingObject {
    // Instance Variables
    String name;
    String breed;
    int age;
    String color;

    // Constructor Declaration of Class
    public InitializingObject(String name, String breed, int age, String color) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.color = color;
    }

    // method 1
    public String getName() {
        return name;
    }

    // method 2
    public String getBreed() {
        return breed;
    }

    // method 3
    public int getAge() {
        return age;
    }

    // method 4
    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return ("Hi my name is " + this.getName() +
                ".\nMy breed,age and color are " +
                this.getBreed() + "," + this.getAge() +
                "," + this.getColor());
    }

    public static void main(String[] args) {
        InitializingObject tuffy = new InitializingObject("tuffy", "papillon", 5, "white");
        System.out.println(tuffy.getName());
        System.out.println(tuffy.getBreed());
        System.out.println(tuffy.getAge());
        System.out.println(tuffy.getColor());
        System.out.println(tuffy.toString());
    }


    //OUTPUT
    //tuffy
    //papillon
    //5
    //white
    //Hi my name is tuffy.
    //My breed,age and color are papillon,5,white
}
