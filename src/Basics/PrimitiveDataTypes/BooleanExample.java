package Basics.PrimitiveDataTypes;

/**Boolean: boolean data type represents only one bit of information either true or false .
 * There are only two possible values: true and false
 * This data type is used for simple flags that track true/false conditions
 * Default value is false
 * Values of type boolean are not converted implicitly or explicitly (with casts) to any other type. But the programmer can easily write conversion code.
 * Most of time boolean data type is use for condition checking.**/

public class BooleanExample {

    public static void main(String[] args) {

        boolean check = true;
        if (check) {
            System.out.println("CHECK");
        }
        if (check == true) {
            System.out.println("CHECK2");
            check = true;
        }

        if (!check) {
            System.out.println("CHECK3");
        } else {
            System.out.println("UNCHECK");
        }

        //OUTPUT
        //CHECK
        //CHECK2
        //UNCHECK
    }
}
