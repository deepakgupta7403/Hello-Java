package OOPSConcepts.ClassesAndObject;

/**
 * There are four ways to create objects in java.
 * Strictly speaking there is only one way(by using new keyword),and the rest internally use new keyword.
 * <p>
 * 1) Using new keyword.
 * 2) Using Class.forName(String className) method
 * 3) Using clone() method
 * 4) Deserialization
 * 5) Using newInstance() method of Constructor class
 **/

public class WaysToCreateObject {

    public static void main(String[] args) {
//      1) Using new keyword.
        NewKeyword keyword = new NewKeyword();
        System.out.println(keyword.name);

//      2) Using new instance  or Using Class.forName(String className) method.
        {
            try {
                Class cls = Class.forName("NewInstance");
                NewInstance obj = (NewInstance) cls.newInstance();
                System.out.println(obj.name);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

//      3) Using clone() method
        Clone obj1 = new Clone();
        try
        {
            Clone obj2 = (Clone) obj1.clone();
            System.out.println(obj2.name);
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }

    }
}

class NewKeyword {
    /**
     * Using new keyword is the most basic way to create an object. This is the most common way to create an object in java. Almost 99% of objects are created in this way.
     * By using this method we can call any constructor we want to call (no argument or parameterized constructors).
     */

    String name = "Deepak Gupta";
}

class NewInstance {
    /**
     * If we know the name of the class & if it has a public default constructor we can create an object –Class.forName.
     * We can use it to create the Object of a Class. Class.forName actually loads the Class in Java but doesn’t create any Object.
     * To Create an Object of the Class you have to use the new Instance Method of the Class.
     */
    String name = "Deepak Gupta";
}

class Clone implements Cloneable {
    /**
     * Whenever clone() is called on any object, the JVM actually creates a new object and copies all content of the previous object into it.
     * Creating an object using the clone method does not invoke any constructor.
     * To use clone() method on an object we need to implement Cloneable and define the clone() method in it.
     * @return
     * @throws CloneNotSupportedException
     */

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    String name = "Deepak Gupta";
}
