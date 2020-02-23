package Basics.PrimitiveDataTypes;

/**Integer (int) :- It is a 32-bit signed two’s complement integer.
 * Size: 32 bit
 * Value: -2^31 to 2^31-1
 * -2.147 billion to 2.147 billion
 * Min Value = -2,147,483,648  || Max Value = 2,147,483,647
 * Integer is generally used as the default data type for integral values unless there is a concern about memory.
 * The default value is 0
 * If we want number to large number readable then we use underscore(_) like 555555555 then we wright it as 5_55_555_555  this feature come out since JAVA 7.
 * Note: In Java SE 8 and later, we can use the int data type to represent an unsigned 32-bit integer, which has value in range [0, 2^32-1]. Use the Integer class to use int data type as an unsigned integer.**/

public class IntegerExample {
    public static void main(String[] args) {
        int minValue = -2147483648;
        int maxValue = 2147483647;

        // int is 32 bit value
        System.out.println(minValue);
        System.out.println(maxValue);


        // It overflows here because
        // int can hold values from -2147483648 to 2147483647
        minValue++;
        maxValue++;
        System.out.println(minValue);
        System.out.println(maxValue);

        // Looping back within the range
        minValue++;
        maxValue++;
        System.out.println(minValue);
        System.out.println(maxValue);

        //OUTPUT
        //-2147483648
        //2147483647
        //-2147483647
        //-2147483648
        //-2147483646
        //-2147483647
    }
}
