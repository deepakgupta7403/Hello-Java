package Basics.Enumerations;

public class InsideClassEnum {

    //Simple enum example where enum is declared inside of the class.

    enum Colors{
        RED,
        ORANGE,
        PINK,
        BLACK,
        WHITE;
    }

    public static void main(String args[]) {
        Colors colors = Colors.PINK;
        System.out.println(colors);

        //OUTPUT
        //PINK

    }
}
