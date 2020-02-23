package Basics.PrimitiveDataTypes;

/**The double data type is a double-precision 64-bit IEEE 754 floating point.
 * This data type is generally used as the default data type for decimal values, generally the default choice
 * Double data type should never be used for precise values such as currency
 * Default value is 0.0d
 * **/

public class DoubleExample {

    public static void main(String[] args) {
        float minValue = -9223370.2F;
        float maxValue = 92233.257f;

        // float is 32 bit value
        System.out.println(minValue);
        System.out.println(maxValue);


        // It overflows here because
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
        //-9223370.0
        //92233.26
        //-9223369.0
        //92234.26
        //-9223368.0
        //92235.26

    }
}

