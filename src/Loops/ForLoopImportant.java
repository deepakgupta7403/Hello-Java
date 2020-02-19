package Loops;
/**The structure of basic for loop:

 for(initialization; boolean expression; update statement)
 {
 //Body
 }**/
public class ForLoopImportant {

//   1)Providing expression in for loop is must :
    /** For loop must consist a valid expression in the loop statement failing which can lead to an infinite loop. The statement
     *
     * for ( ; ; )
     * is similar to
     * while(true)**/

    class Example1
    {
        public void InfiniteLoopMethod()
        {
            for( ; ; )
            {
                System.out.println("This is an infinite loop");
            }
        }
        //Output
        // This code prints the statement “This is an infinite loop” repeatedly.
    }



    //   2)Initializing multiple variables
    /** In Java, multiple variables can be initialized in initialization block of for loop regardless of whether you use it in the loop or not.**/
    class Example2
    {

        public void MultipleVariableAssignment()
        {
            int x = 2;
            for(long y = 0, z = 4; x < 10 && y < 10; x++, y++)
            {
                System.out.println(y + " ");
            }
            System.out.println(x);
        }

        /**In the above code, there is simple variation in the for loop. Two variables are declared and initialized in the initialization block.
         * The variable ‘z’ is not being used. Also, the other two components contain extra variable.
         * So, it can be seen that the blocks may include extra variables which may not be referenced by each other.**/
    }



    // 3) Redeclaration of a variable in initialization block
    /**Suppose, an initialization vaiable is already declared as integer.
     * Can we re-declare it in for loop with other data type? No, See the example:**/

    class Example3
    {
        public void WrongDeclaration(String[] args)
        {
            // x is integer
            int x = 0;

            // redeclaring x as long will not work
           /* for(long y = 0, x = 1; x < 5; x++)
            {
                System.out.print(x + " ");
            }*/

           //error: variable x is already defined in method main(String[])
            //        for(long y = 0, x = 1; x < 5; x++)

        }
    }



    // 3) Variables declared in the initialization block must be of same type
    /**It is just a common sense that when we declare a variable as both variables are of same type.
     * Its just the same in for loop initialization block too.**/

    public class Example4
    {
        public void SameDataType()
        {
            // This will cause error;
            // int x;

            // redeclaring x as long will not work
            for (long y = 0, x = 1; x < 5; x++)
            {
                System.out.print(x + " ");
            }

        }
    }
}
