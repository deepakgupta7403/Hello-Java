package Basics.Loops;

/**
 * For loop provides a concise way of writing the loop structure.
 * Unlike a while loop, a for statement consumes the initialization,
 * condition and increment/decrement in one line thereby providing a shorter, easy to debug structure of looping.
 *
 *
 * Syntax:
 * for (initialization condition; testing condition; increment/decrement)
 * {
 *     statement(s)
 * }
 *
 * FlowChart
 *
 * Start---->Initialization---->Condition Checking---->if true---->Loop body---->Increment/Decrement---->false---->Exit from the Loop
 * Initialization condition: Here, we initialize the variable in use. It marks the start of a for loop. An already declared variable can be used or a variable can be declared, local to loop only.
 * Testing Condition: It is used for testing the exit condition for a loop. It must return a boolean value. It is also an Entry Control Loop as the condition is checked prior to the execution of the loop statements.
 * Statement execution: Once the condition is evaluated to true, the statements in the loop body are executed.
 * Increment/ Decrement: It is used for updating the variable for next iteration.
 * Loop termination:When the condition becomes false, the loop terminates marking the end of its life cycle.
 ***/

public class ForLoop {
    public static void main(String args[])
    {
        // for loop begins when x=2
        // and runs till x <=4
        for (int x = -10; x <= 4; x++)
            System.out.println("Value of x:" + x);


        //OUTPUT
        //Value of x:-10
        //Value of x:-9
        //Value of x:-8
        //Value of x:-7
        //Value of x:-6
        //Value of x:-5
        //Value of x:-4
        //Value of x:-3
        //Value of x:-2
        //Value of x:-1
        //Value of x:0
        //Value of x:1
        //Value of x:2
        //Value of x:3
        //Value of x:4
    }
}
