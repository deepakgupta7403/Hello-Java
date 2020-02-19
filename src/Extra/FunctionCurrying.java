package Extra;

import java.util.function.Function;

/**
 * Function Currying is a concept of breaking a function with many arguments into many functions with single argument in such a way, that the output is same.
 * In other words, its a technique of simplifying a multi-valued argument function into single-valued argument multi-functions.
 * example:-
 * addThreeNumberFun(a,b,c){(return a+b+c)} -----After applying Function Currying addNumberOne(a){return addnumberTwo(b) return addNumberThree(c){return (a+b+c)}}}
 *
 * Currying breaks down higher order functions into a series of smaller cascaded functions which take in one argument and return a function except for the last cascaded function which returns the desired value.
 *
 * **/


public class FunctionCurrying {

    public static void main(String[] args) {

        FunctionCurrying functionCurrying = new FunctionCurrying();
        functionCurrying.add();
        functionCurrying.multiply();
        functionCurrying.triadder();
    }


    //Adding 2 numbers using Function Currying
    private void add() {
        // Using Java 8 Functions
        // to create lambda expressions for functions
        // and with this, applying Function Currying

        // Curried Function for Adding u & v
        Function<Integer,
                Function<Integer, Integer>>
                curryAdder = u -> v -> u + v;

        // Calling the curried functions

        // Calling Curried Function for Adding u & v
        System.out.println("Add 2, 3 :"
                + curryAdder
                .apply(2)
                .apply(3));


        //OUTPUT
        //Add 2, 3 :5
    }


    //Multiplying 2 numbers using Function Currying
    private void multiply() {
        // Using Java 8 Functions
        // to create lambda expressions for functions
        // and with this, applying Function Currying

        // Curried Function for Multiplying u & v
        Function<Integer,
                Function<Integer, Integer> >
                curryMulti = u -> v -> u * v;

        // Calling the curried functions

        // Calling Curried Function for Multiplying u & v
        System.out.println("Multiply 2, 3 :"
                + curryMulti
                .apply(2)
                .apply(3));



        //OUTPUT
        //Multiply 2, 3 :6
    }


    //Adding 3 numbers using Function Currying
    private void triadder() {
        // Using Java 8 Functions
        // to create lambda expressions for functions
        // and with this, applying Function Currying

        // Curried Function for Adding u, v & w
        Function<Integer,
                Function<Integer,
                        Function<Integer, Integer> > >
                triadder = u -> w -> v -> u + w + v;

        // Calling the curried functions

        // Calling Curried Function for Adding u, v & w
        System.out.println("Add 2, 3, 4 :"
                + triadder
                .apply(2)
                .apply(3)
                .apply(4));


        //OUTPUT
        //Add 2, 3, 4 :9
    }
}
