package DecisionMaking;

/**A nested if is an if statement that is the target of another if or else.
 * Nested if statements means an if statement inside an if statement.
 * Yes, java allows us to nest if statements within if statements.
 * i.e, we can place an if statement inside another if statement.
 *
 Syntax:
 if (condition1)
 {
 // Executes when condition1 is true
 if (condition2)
 {
 // Executes when condition2 is true
 }
 }**/

public class NestedIfStatement {
    public static void main(String args[])
    {
        int i = 10;

        if (i == 10)
        {
            // First if statement
            if (i < 15)
                System.out.println("i is smaller than 15");

            // Nested - if statement
            // Will only be executed if statement above
            // it is true
            if (i < 12)
                System.out.println("i is smaller than 12 too");
            else
                System.out.println("i is greater than 15");
        }
    }


    //OUTPUT
    //i is smaller than 15
    //i is smaller than 12 too
}
