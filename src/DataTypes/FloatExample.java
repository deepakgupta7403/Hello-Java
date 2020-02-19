package DataTypes;

/**Float data type is a single-precision 32-bit IEEE 754 floating point
 * Use a float (instead of double) if you need to save memory in large arrays of floating point numbers.
 * Size: 32 bits
 * Suffix : F/f Example: 9.8f
 * Float is mainly used to save memory in large arrays of floating point numbers
 * Default value is 0.0f
 * Float data type is never used for precise values such as currency**/

public class FloatExample {
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
