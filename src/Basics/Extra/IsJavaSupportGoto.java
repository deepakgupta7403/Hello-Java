package Basics.Extra;

/**
 * Java does not support goto, it is reserved as a keyword just in case they wanted to add it to a later version.
 *  Unlike C/C++, Java does not have goto statement, but java supports label.
 *  The only place where a label is useful in Java is right before nested loop statements.
 *  We can specify label name with break to break out a specific outer loop.
 *  Similarly, label name can be specified with continue.**/

public class IsJavaSupportGoto {



    public static void main(String[] args) {
        IsJavaSupportGoto isJavaSupportGoto = new IsJavaSupportGoto();
        isJavaSupportGoto.usingBreak();
        isJavaSupportGoto.usingContinue();

    }


    //Using break with label in Java
    private void usingBreak() {
        // label for outer loop
        outer:
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (j == 1)
                    break outer;
                System.out.println(" value of j = " + j);
            }
        }

        // OUTPUT
        // value of j = 0
    }

    //Using continue with label in Java
    private void usingContinue() {
        // label for outer loop
        outer:
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (j == 1)
                    continue outer;
                System.out.println(" value of j = " + j);
            }
        }

        //OUTPUT
        //value of j = 0
        //value of j = 0
        //value of j = 0
        //value of j = 0
        //value of j = 0
        //value of j = 0
        //value of j = 0
        //value of j = 0
        //value of j = 0
        //value of j = 0


        //Since continue statement skips to the next iteration in the loop, it iterates for 10 times as i iterates from 0 to 9.
        // So the outer loop executes for 10 times and the inner for loop executes 1 time in each of the outer loop.
    }
}
