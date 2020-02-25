package Basics.Enumerations;

enum Colors{
    RED,
    ORANGE,
    PINK,
    BLACK,
    WHITE;
}

public class OutsideClassEnum {

    //Declaration of enum in java :
    //Enum declaration can be done outside a Class or inside a Class but not inside a Method.

    //Simple enum example where enum is declared outside of the class.


    public static void main(String args[]) {
        Colors colors = Colors.RED;
        System.out.println(colors);

        //OUTPUT
        //RED

    }
}
