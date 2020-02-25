package Basics.Conversion;

/**
 * Widening conversion takes place when two data types are automatically converted. This happens when:
 *
 * The two data types are compatible.
 * When we assign value of a smaller data type to a bigger data type.
 * For Example, in java the numeric data types are compatible with each other but no automatic conversion is supported from numeric type to char or boolean.
 * Also, char and boolean are not compatible with each other.
 *
 *   Byte------->Short------->Int------->Long-------->Float------->Double
 *   **/

public class AutomaticTypeConversion {
    public static void main(String[] args)
    {
        int i = 100;

        //automatic type conversion
        long l = i;

        //automatic type conversion
        float f = l;
        System.out.println("Int value "+i);
        System.out.println("Long value "+l);
        System.out.println("Float value "+f);
    }

    //OUTPUT
    //Int value 100
    //Long value 100
    //Float value 100.0
}
