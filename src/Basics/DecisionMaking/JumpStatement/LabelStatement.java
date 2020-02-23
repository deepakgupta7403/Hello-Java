package Basics.DecisionMaking.JumpStatement;

/**Java does not have a goto statement because it provides a way to branch in an arbitrary and unstructured manner.
 * Java uses label. A Label is use to identifies a block of code.
 Syntax:

 label:
 {
 statement1;
 statement2;
 statement3;
 .
 .
 }

 Now, break statement can be use to jump out of target block.
 Note: You cannot break to any label which is not defined for an enclosing block.
 Syntax:

 break label;**/

public class LabelStatement {

    public static void main(String args[])
    {
        boolean t = true;

        // label first
        first:
        {
            // Illegal statement here as label second is not
            // introduced yet break second;
            second:
            {
                third:
                {
                    // Before break
                    System.out.println("Before the break statement");

                    // break will take the control out of
                    // second label
                    if (t)
                        break second;
                    System.out.println("This won't execute.");
                }
                System.out.println("This won't execute.");
            }

            // Third block
            System.out.println("This is after second block.");
        }
    }

    //OUTPUT
    //Before the break.
    //This is after second block.

}
