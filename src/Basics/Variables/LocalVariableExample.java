package Basics.Variables;

/**A variable defined within a block or method or constructor is called local variable.
 * These variable are created when the block in entered or the function is called and destroyed after exiting from the block or when the call returns from the function.
 * The scope of these variables exists only within the block in which the variable is declared. i.e. we can access these variable only within that block.
 * Initialisation of Local Variable is Mandatory.**/

public class LocalVariableExample {
    public void StudentAge() {
        int age = 20;
        System.out.println(age);

        age += 20;
        System.out.println(age);
    }

    public static void main(String[] args) {
        LocalVariableExample example = new LocalVariableExample();
        example.StudentAge();
    }


    //OUTPUT
    //20
    //40
}
