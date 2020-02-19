package Conversion;

/**
 * If we want to assign a value of larger data type to a smaller data type we perform explicit type casting or narrowing.
 * <p>
 * This is useful for incompatible data types where automatic conversion cannot be done.
 * Here, target-type specifies the desired type to convert the specified value to.
 * <p>
 * Dobule-------->Float-------->Long-------->Int-------->Short-------->Byte
 **/

public class ExplicitTypeConversion {

    public static void main(String[] argv) {
//        char ch = 'c';
//////        int num = 88;
//////        ch = num;
//////
//////        //OUTPUT
//////        //error: incompatible types: possible lossy conversion from int to char
//////        //    ch = num;

        double d = 100.04;

        //explicit type casting
        long l = (long) d;

        //explicit type casting
        int i = (int) l;
        System.out.println("Double value " + d);

        //fractional part lost
        System.out.println("Long value " + l);

        //fractional part lost
        System.out.println("Int value " + i);

        ExplicitTypeConversion typeConversion = new ExplicitTypeConversion();
        typeConversion.explicitTypeConversion();


        //OUTPUT
        //Double value 100.04
        //Long value 100
        //Int value 100
    }


    //While assigning value to byte type the fractional part is lost and is reduced to modulo 256(range of byte).

    private void explicitTypeConversion() {
        byte b;
        int i = 257;
        double d = 323.142;
        System.out.println("Conversion of int to byte.");

        //i%256
        b = (byte) i;
        System.out.println("i = " + i + " b = " + b);
        System.out.println("\nConversion of double to byte.");

        //d%256
        b = (byte) d;
        System.out.println("d = " + d + " b= " + b);


        //OUTPUT
        //Conversion of int to byte.
        //i = 257 b = 1
        //
        //Conversion of double to byte.
        //d = 323.142 b= 67
    }
}
