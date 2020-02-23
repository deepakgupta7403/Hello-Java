package OOPSConcepts.ClassesAndObject;

/**
 * There are four ways to create objects in java.
 * Strictly speaking there is only one way(by using new keyword),and the rest internally use new keyword.
 * <p>
 * 1) Using new keyword.
 * 2) Using Class.forName(String className) method
 * 3) Using clone() method
 * 4) Deserialization
 **/

public class WaysToCreateObject {

    public static void main(String[] args) {

    }


    // 1)Using new keyword : It is the most common and general way to create object in java.
    WaysToCreateObject usingNewKey = new WaysToCreateObject();

    //2)Using Class.forName(String className) method:
    /**
     * There is a pre-defined class in java.lang package with name Class.
     * The forName(String className) method returns the Class object associated with the class with the given string name.
     * We have to give the fully qualified name for a class.
     * On calling new Instance() method on this Class object returns new instance of the class with the given string name.
     **/
    // consider class Test present in com.p1 package
    //WaysToCreateObject usingForName = (WaysToCreateObject)Class.forName("com.p1").newInstance();
    WaysToCreateObject usingForName;

    /**public class NewInstanceExample
     {
     String name = "GeeksForGeeks";
     public static void main(String[] args)
     {
     try
     {
     Class cls = Class.forName("NewInstanceExample");
     NewInstanceExample obj =
     (NewInstanceExample) cls.newInstance();
     System.out.println(obj.name);
     }
     catch (ClassNotFoundException e)
     {
     e.printStackTrace();
     }
     catch (InstantiationException e)
     {
     e.printStackTrace();
     }
     catch (IllegalAccessException e)
     {
     e.printStackTrace();
     }
     }
     } **/ {
        try {
            usingForName = (WaysToCreateObject) Class.forName("com.p1").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //3) Using clone() method: clone() method is present in Object class. It creates and returns a copy of the object.
    // creating object of class Test
    WaysToCreateObject clone = new WaysToCreateObject();
    /**public class CloneExample implements Cloneable
     {
     @Override
     protected Object clone() throws CloneNotSupportedException
     {
     return super.clone();
     }
     String name = "GeeksForGeeks";

     public static void main(String[] args)
     {
     CloneExample obj1 = new CloneExample();
     try
     {
     CloneExample obj2 = (CloneExample) obj1.clone();
     System.out.println(obj2.name);
     }
     catch (CloneNotSupportedException e)
     {
     e.printStackTrace();
     }
     }
     } **/

    // creating clone of above object
    //WaysToCreateObject clonecopy = (WaysToCreateObject)clone.clone();
    WaysToCreateObject clonecopy;

    {
        try {
            clonecopy = (WaysToCreateObject) clone.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }


    //4) Deserialization
    /** De-serialization is technique of reading an object from the saved state in a file.
     * Refer Serialization/De-Serialization in java**/
    /*Using deserialization : Whenever we serialize and then deserialize an object, JVM creates a separate object. In deserialization, JVM doesn’t use any constructor to create the object.
    To deserialize an object we need to implement the Serializable interface in the class.

    Serializing an Object :

    filter_none
            edit
    play_arrow

            brightness_4
// Java program to illustrate Serializing
// an Object.
import java.io.*;

    class DeserializationExample implements Serializable
    {
        private String name;
        DeserializationExample(String name)
        {
            this.name = name;
        }

        public static void main(String[] args)
        {
            try
            {
                DeserializationExample d =
                        new DeserializationExample("GeeksForGeeks");
                FileOutputStream f = new FileOutputStream("file.txt");
                ObjectOutputStream oos = new ObjectOutputStream(f);
                oos.writeObject(d);
                oos.close();
                f.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    Object of DeserializationExample class is serialized using writeObject() method and written to file.txt file.

    Deserialization of Object :

    filter_none
            edit
    play_arrow

            brightness_4
// Java program to illustrate creation of Object
// using Deserialization.
import java.io.*;

    public class DeserializationExample
    {
        public static void main(String[] args)
        {
            try
            {
                DeserializationExample d;
                FileInputStream f = new FileInputStream("file.txt");
                ObjectInputStream oos = new ObjectInputStream(f);
                d = (DeserializationExample)oos.readObject();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            System.out.println(d.name);
        }
    }*/





}
