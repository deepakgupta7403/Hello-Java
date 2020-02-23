package OOPSConcepts.Inheritance.TypesOfInheritance;

/**
 * In Multiple inheritance ,one class can have more than one superclass and inherit features from all parent classes.
 * Please note that Java does not support multiple inheritance with classes.
 * In java, we can achieve multiple inheritance only through Interfaces.
 * below, Class C is derived from interface A and B.
 *
 * Class A----------------------------->|
 *                                      |Class C
 * Class B----------------------------->|**/


// Java program to illustrate the
// concept of Multiple inheritance
import java.lang.*;

interface Intone{
    public void print_geeks();
}

interface Inttwo {
    public void print_for();
}

interface Intthree extends Intone,Inttwo {
    public void print_geek();
}

class Child implements Intthree {

    @Override
    public void print_for() {
        System.out.println("for");
    }

    @Override
    public void print_geek() {
        System.out.println("Geek");
    }

    @Override
    public void print_geeks() {
        System.out.println("Geeks");
    }
}

public class MultipleInheritance {
    public static void main(String[] args)
    {
        Child c = new Child();
        c.print_geek();
        c.print_for();
        c.print_geeks();
    }


    //OUTPUT
    //Geeks
    //for
    //Geeks
}
