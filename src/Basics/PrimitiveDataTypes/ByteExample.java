package Basics.PrimitiveDataTypes;

/**Byte :- The byte data type is an 8-bit signed two’s complement integer.
 * The byte data type is useful for saving memory in large arrays. Mainly in place of integers, since a byte is four times smaller than an integer.
 * Minimum value is -128 (-2^7)
 * Maximum value is 127 (inclusive)(2^7 -1)
 * Default value is 0
 * If The byte value increase more then 127 the it's showing error incompatible type because BYTE hold value between -128 to 127 so after 127 it convert to int.
 * if i need to use 300 as Byte so i need to cast the 300 into byte like this ...   byte be = (byte) 300   **/

public class ByteExample {

    public static void main(String args[])
    {

        byte a = 126;
        /*byte a = 300;*/

        // byte is 8 bit value
        System.out.println(a);

        a++;
        System.out.println(a);

        // It overflows here because
        // byte can hold values from -128 to 127
        a++;
        System.out.println(a);

        // Looping back within the range
        a++;
        System.out.println(a);

        //OUTPUT
        //126
        //127
        //-128
        //-127
    }
}
