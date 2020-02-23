package Basics.DecisionMaking.JumpStatement;

/**In Java, break is majorly used for:
 Terminate a sequence in a switch statement (discussed above).
 To exit a loop.
 Used as a “civilized” form of goto.
 Using break to exit a Loop

 Using break, we can force immediate termination of a loop, bypassing the conditional expression and any remaining code in the body of the loop.
 Note: Break, when used inside a set of nested loops, will only break out of the innermost loop.**/

public class BreakStatement {
    public static void main(String args[])
    {
        // Initially loop is set to run from 0-9
        for (int i = 0; i < 10; i++)
        {
            // terminate loop when i is 5.
            if (i == 5)
                break;

            System.out.println("i: " + i);
        }
        System.out.println("Loop complete.");
    }

    //OUTPUT
    //i: 0
    //i: 1
    //i: 2
    //i: 3
    //i: 4
    //Loop complete.
}
