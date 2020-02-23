package Basics.Enumerations;

/**values(), ordinal() and valueOf() methods :

 These methods are present inside java.lang.Enum.
 values() method can be used to return all values present inside enum.
 Order is important in enums.By using ordinal() method, each enum constant index can be found, just like array index.
 valueOf() method returns the enum constant of the specified string value, if exists.
 filter_none
 edit
 play_arrow

 brightness_4
 // Java program to demonstrate working of values(),
 // ordinal() and valueOf()
 enum Color **/

public class EnumMethods {
    public static void main(String[] args)
    {
        // Calling values()
        MainInsideEnum arr[] = MainInsideEnum.values();

        // enum with loop
        for (MainInsideEnum col : arr)
        {
            // Calling ordinal() to find index
            // of color.
            System.out.println(col + " at index "
                    + col.ordinal());
        }

        // Using valueOf(). Returns an object of
        // Color with given constant.
        // Uncommenting second line causes exception
        // IllegalArgumentException
        System.out.println(MainInsideEnum.valueOf("RED"));
        // System.out.println(Color.valueOf("WHITE"));


        // OUTPUT
        //RED at index 0
        //GREEN at index 1
        //BLUE at index 2
        //RED
    }
}
