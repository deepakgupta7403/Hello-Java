package OOPSConcepts.Inheritance.TypesOfInheritance;

/**In Hierarchical OOPSConcepts.Inheritance, one class serves as a superclass (base class) for more than one sub class.
 * In below image, the class A serves as a base class for the derived class B,C and D.
 *
 * Class A
 *  |--------Class B
 *  |--------Class C
 *  |--------Class D
 *
 *  **/


// Java program to illustrate the
// concept of Hierarchical inheritance
import java.lang.*;

class AOne
{
    public void print_geek()
    {
        System.out.println("Geeks");
    }
}

class BTwo extends AOne
{
    public void print_for()
    {
        System.out.println("for");
    }
}

class CThree extends BTwo
{
    /*............*/
}


//Derived Class
public class HierarchicalInheritance {
    public static void main(String[] args)
    {
        CThree g = new CThree();
        g.print_geek();
        BTwo t = new BTwo();
        t.print_for();
        g.print_geek();
    }



    //OUTPUT
    //Geeks
    //for
    //Geeks

}
