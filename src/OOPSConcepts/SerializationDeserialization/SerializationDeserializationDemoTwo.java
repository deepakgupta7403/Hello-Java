package OOPSConcepts.SerializationDeserialization;

import java.io.*;

public class SerializationDeserializationDemoTwo {
    public static void printdata(DemoTwoModel object1)
    {

        System.out.println("name = " + object1.name);
        System.out.println("age = " + object1.age);
        System.out.println("a = " + object1.a);
        System.out.println("b = " + object1.b);
    }

    public static void main(String[] args)
    {
        DemoTwoModel object = new DemoTwoModel("ab", 20, 2, 1000);
        String filename = "Deepak.txt";

        // Serialization
        try {

            // Saving of object in a file
            FileOutputStream file = new FileOutputStream
                    (filename);
            ObjectOutputStream out = new ObjectOutputStream
                    (file);

            // Method for serialization of object
            out.writeObject(object);

            out.close();
            file.close();

            System.out.println("Object has been serialized\n"
                    + "Data before Deserialization.");
            printdata(object);

            // value of static variable changed
            object.b = 2000;
        }

        catch (IOException ex) {
            System.out.println("IOException is caught");
        }

        object = null;

        // Deserialization
        try {

            // Reading the object from a file
            FileInputStream file = new FileInputStream
                    (filename);
            ObjectInputStream in = new ObjectInputStream
                    (file);

            // Method for deserialization of object
            object = (DemoTwoModel)in.readObject();

            in.close();
            file.close();
            System.out.println("Object has been deserialized\n"
                    + "Data after Deserialization.");
            printdata(object);

            // System.out.println("z = " + object1.z);
        }

        catch (IOException ex) {
            System.out.println("IOException is caught");
        }

        catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException" +
                    " is caught");
        }
    }

//    OUTPUT
//    Object has been serialized
//    Data before Deserialization.
//    name = ab
//    age = 20
//    a = 2
//    b = 1000
//    Object has been deserialized
//    Data after Deserialization.
//    name = ab
//    age = 20
//    a = 0
//    b = 2000
}
