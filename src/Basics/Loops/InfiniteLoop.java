package Basics.Loops;

import java.util.ArrayList;

/**One of the most common mistakes while implementing any sort of looping is that that it may not ever exit,
 * that is the loop runs for infinite time. This happens when the condition fails for some reason.**/

public class InfiniteLoop {
    public static void main(String[] args)
    {

        // infinite loop because condition is not apt
        // condition should have been i>0.
        for (int i = 5; i != 0; i += 2)
        {
            System.out.println(i);
        }
        int x = 5;

        // infinite loop because update statement
        // is not provided.
        while (x == 5)
        {
            System.out.println("In the loop");
        }


        //OUTPUT
        //Only for loop is executing because the for loop is infinite loop and before terminating while loop is not executing.
        //5
        //7
        //9
        //11
        //13
        //15
        //17
        //19
        //21
        //23
        //25
        //27
        //Many More n Number
        //Ctrl + c or terminate the execution of class then it'll exit from the loop

        ArrayList<Integer> ar = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++)
        {
            ar.add(i);
        }


        //OUTPUT
        //Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        //at java.util.Arrays.copyOf(Unknown Source)
        //at java.util.Arrays.copyOf(Unknown Source)
        //at java.util.ArrayList.grow(Unknown Source)
        //at java.util.ArrayList.ensureCapacityInternal(Unknown Source)
        //at java.util.ArrayList.add(Unknown Source)
        //at article.Integer1.main(Integer1.java:9)
    }
}
