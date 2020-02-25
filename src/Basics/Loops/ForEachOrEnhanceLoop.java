package Basics.Loops;

/**Java also includes another version of for loop introduced in Java 5.
 * Enhanced for loop provides a simpler way to iterate through the elements of a collection or array.
 * It is inflexible and should be used only when there is a need to iterate through the elements in sequential manner without knowing the index of currently processed element.
 * Also note that the object/variable is immutable when enhanced for loop is used i.e it ensures that the values in the array can not be modified,
 * so it can be said as read only loop where you can’t update the values as opposite to other loops where values can be modified.
 * We recommend using this form of the for statement instead of the general form whenever possible.(as per JAVA doc.)
 *
 * Syntax:
 * for (T element:Collection obj/array)
 * {
 *     statement(s)
 * }**/

public class ForEachOrEnhanceLoop {
    public static void main(String args[])
    {
        String array[] = {"SHYAMDHAR","SANGEETA","DEEPAK", "JYOTI", "SURAJ"};

        //enhanced for loop
        for (String x:array)
        {
            System.out.println(x);
        }

        /* for loop for same function
        for (int i = 0; i < array.length; i++)
        {
            System.out.println(array[i]);
        }
        */



        //OUTPUT
        //SHYAMDHAR
        //SANGEETA
        //DEEPAK
        //JYOTI
        //SURAJ
    }

    //OUTPUT

}
