package OOPSConcepts.SerializationDeserialization;

import java.io.*;

public class SerializationDeserializationDemoOne {
    public static void main(String[] args)
    {
        DemoOneModel object = new DemoOneModel(1, "Deepak Java");
        String filename = "file.zip";

        // Serialization
        try
        {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(object);

            out.close();
            file.close();

            System.out.println("Object has been serialized");

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }


        DemoOneModel object1 = null;

        // Deserialization
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            object1 = (DemoOneModel) in.readObject();

            in.close();
            file.close();

            System.out.println("Object has been deserialized ");
            System.out.println("a = " + object1.value);
            System.out.println("b = " + object1.key);
        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }



//        OUTPUT
//        Object has been serialized
//        Object has been deserialized
//        a = 1
//        b = Deepak Java

    }
}
