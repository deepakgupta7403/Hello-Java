package Conversion;

/**
 * When we use double quotes, the text is treated as a string and “YO” is printed, but when we use single quotes, the characters ‘L’ and ‘O’ are converted to int.
 * This is called widening primitive conversion.
 * After conversion to integer, the numbers are added ( ‘L’ is 76 and ‘O’ is 79) and 155 is printed.
 **/

public class WideningPrimitive {
    public static void main(String[] args) {
        System.out.print("Y" + "O");
        System.out.print('L' + 'O');

        //OUTPUT
        //YO155
    }


    private void anotherExample() {
        System.out.print("Y" + "O");
        System.out.print('L');
        System.out.print('O');

        //OUTPUT and Explanation is given below
        //YOLO

        /**
         * This will now print “YOLO” instead of “YO7679”.
         * It is because the widening primitive conversion happens only when a operator like ‘+’ is present which expects at least integer on both side.
         *
         * Widening primitive conversion is applied to convert either or both operands as specified by the following rules.
         * The result of adding Java chars, shorts or bytes is an int:
         *
         * If either operand is of type double, the other is converted to double.
         * Otherwise, if either operand is of type float, the other is converted to float.
         * Otherwise, if either operand is of type long, the other is converted to long.
         * Otherwise, both operands are converted to type int**/

    }

}
