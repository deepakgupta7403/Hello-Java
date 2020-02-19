package DataTypes;

/**Short:- The short data type is a 16-bit signed two’s complement integer.
 * Similar to byte, use a short to save memory in large arrays, in situations where the memory savings actually matters.
 * If The short value increase more then 327767 the it's showing error incompatible type because SHORT hold value between -32,768 to 32,767 so after 32,767 it convert to int.
 * if i need to use 32,768 as short so i need to cast the 32,768 into short like this ...   short be = (short) 32768
 * Size: 16 bit
 * Value: -32,768 to 32,767 (inclusive)
 * Short data type can also be used to save memory as byte data type. A short is 2 times smaller than an integer
 * Default value is 0.**/
public class ShortExample {

    public static void main(String args[])
    {

        short a = 32766;
        /*short a = 32768;*/

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
        //32766
        //32767
        //-32768
        //-32767
    }
}
