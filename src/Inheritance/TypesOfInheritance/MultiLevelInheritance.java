package Inheritance.TypesOfInheritance;

/**In Multilevel Inheritance, a derived class will be inheriting a base class and as well as the derived class also act as the base class to other class.
 * In below image, the class A serves as a base class for the derived class B, which in turn serves as a base class for the derived class C.
 * In Java, a class cannot directly access the grandparent’s members.
 *
 * Class A(Base Class) -----------------------> Class B(Intermediary Class) --------------------------> Class C(Derived Class) **/


// Java program to illustrate the
// concept of Multilevel inheritance

import java.lang.*;

class A
{
    public void print_geek()
    {
        System.out.println("Geeks");
    }
}

class B extends A
{
    public void print_for()
    {
        System.out.println("for");
    }
}

class C extends B
{
    public void print_geeks()
    {
        System.out.println("Geeks");
    }
}

// Drived class
public class MultiLevelInheritance {
    public static void main(String[] args)
    {
        C c = new C();
        c.print_geek();
        c.print_for();
        c.print_geeks();
    }



    //OUTPUT
    //Geeks
    //for
    //Geeks
}
