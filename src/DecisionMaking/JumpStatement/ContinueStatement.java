package DecisionMaking.JumpStatement;

/**Sometimes it is useful to force an early iteration of a loop.
 * That is, you might want to continue running the loop but stop processing the remainder of the code in its body for this particular iteration.
 * This is, in effect, a goto just past the body of the loop, to the loop’s end.
 * The continue statement performs such an action.**/

public class ContinueStatement {
    public static void main(String args[])
    {
        for (int i = 0; i < 10; i++)
        {
            // If the number is even
            // skip and continue
            if (i%2 == 0)
                continue;

            // If number is odd, print it
            System.out.print(i + " ");
        }
    }

    //OUTPUT
    //1 3 5 7 9
}
