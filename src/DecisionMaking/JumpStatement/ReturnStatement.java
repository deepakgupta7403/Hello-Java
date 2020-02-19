package DecisionMaking.JumpStatement;

/**The return statement is used to explicitly return from a method. That is, it causes a program control to transfer back to the caller of the method.**/

public class ReturnStatement {
    public static void main(String args[])
    {
        boolean t = true;
        System.out.println("Before the return.");

        if (t)
            return;

        // Compiler will bypass every statement
        // after return
        System.out.println("This won't execute.");
    }


    //OUTPUT
    //Before the return.
}
