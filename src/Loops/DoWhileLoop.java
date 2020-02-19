package Loops;

/**do while loop is similar to while loop with only difference that it checks for condition after executing the statements,
 * and therefore is an example of Exit Control Loop.
 *
 * Syntax:
 * do
 * {
 *     statements..
 * }
 * while (condition);
 *
 *
 * Flowchart:
 * start----> Loop Body----->Condition Checking----->if true----->Loop Body----->Condition Checking----->if false----->Loop exit
 * do while loop starts with the execution of the statement(s). There is no checking of any condition for the first time.
 * After the execution of the statements, and update of the variable value, the condition is checked for true or false value. If it is evaluated to true, next iteration of loop starts.
 * When the condition becomes false, the loop terminates which marks the end of its life cycle.
 * It is important to note that the do-while loop will execute its statements atleast once before any condition is checked, and therefore is an example of exit control loop.**/
public class DoWhileLoop {
    public static void main(String args[])
    {
        int x = 21;
        do
        {
            // The line will be printed even
            // if the condition is false
            System.out.println("Value of x:" + x);
            x++;
        }
        while (x < 20);
    }


    //OUTPUT
    //Value of x: 21
}
