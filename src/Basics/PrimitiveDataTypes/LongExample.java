package Basics.PrimitiveDataTypes;

/**
 * The long data type is a 64-bit two’s complement integer.
 * <p>
 * Size: 64 bit
 * Value: -2^63 to 2^63-1.
 * Minimum value is -9,223,372,036,854,775,808(-2^63)
 * Maximum value is 9,223,372,036,854,775,807 (inclusive)(2^63 -1)
 * This type is used when a wider range than int is needed.
 * Default value is 0L
 * Note: In Java SE 8 and later, you can use the long data type to represent an unsigned 64-bit long,
 * which has a minimum value of 0 and a maximum value of 264-1. The Long class also contains methods like compareUnsigned,
 * divideUnsigned etc to support arithmetic operations for unsigned long.
 **/

public class LongExample {
    public static void main(String[] args) {
        long minValue = -9223372036854775808L;
        long maxValue = 9223372036854775807L;

        // long is 64 bit value
        System.out.println(minValue);
        System.out.println(maxValue);


        // It overflows here because
        // long can hold values from -9223372036854775808 to 9223372036854775807
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
        //-9223372036854775808
        //9223372036854775807
        //-9223372036854775807
        //-9223372036854775808
        //-9223372036854775806
        //-9223372036854775807

    }

}