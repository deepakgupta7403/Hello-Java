package OOPSConcepts.Inheritance.TypesOfInheritance;

/**
 * Hybrid OOPSConcepts.Inheritance(Through Interfaces) : It is a mix of two or more of the above types of inheritance.
 * Since java doesn’t support multiple inheritance with classes, the hybrid inheritance is also not possible with classes.
 * In java, we can achieve hybrid inheritance only through Interfaces.
 * **/

public class HybridInheritance {
    public static void main(String[] args) {
        System.out.println("Important facts about inheritance in Java");
        System.out.println("Default superclass");
        System.out.println("Except Object class, which has no superclass, every class has one and only one direct superclass (single inheritance). In the absence of any other explicit superclass, every class is implicitly a subclass of Object class.");

        System.out.println("Superclass can only be one");
        System.out.println("A superclass can have any number of subclasses. But a subclass can have only one superclass. This is because Java does not support multiple inheritance with classes. Although with interfaces, multiple inheritance is supported by java.");

        System.out.println("Inheriting Constructors");
        System.out.println("A subclass inherits all the members (fields, methods, and nested classes) from its superclass. Constructors are not members, so they are not inherited by subclasses, but the constructor of the superclass can be invoked from the subclass.");

        System.out.println("Private member inheritance");
        System.out.println("A subclass does not inherit the private members of its parent class. However, if the superclass has public or protected methods(like getters and setters) for accessing its private fields, these can also be used by the subclass.");

    }





    /**What all can be done in a Subclass?

     In sub-classes we can inherit members as is, replace them, hide them, or supplement them with new members:

     The inherited fields can be used directly, just like any other fields.
     We can declare new fields in the subclass that are not in the superclass.
     The inherited methods can be used directly as they are.
     We can write a new instance method in the subclass that has the same signature as the one in the superclass, thus overriding it (as in example above, toString() method is overridden).
     We can write a new static method in the subclass that has the same signature as the one in the superclass, thus hiding it.
     We can declare new methods in the subclass that are not in the superclass.
     We can write a subclass constructor that invokes the constructor of the superclass, either implicitly or by using the keyword super.**/
}
