package Extra;

/**A new feature was introduced by JDK 7 which allows to write numeric literals using the underscore character.
 * Numeric literals are broken to enhance the readability.
 * This feature enables us to separate groups of digits in numeric literals, which improves readability of code.
 * For instance, if our code contains numbers with many digits, we can use an underscore character to separate digits in groups of three, similar to how we would use a punctuation mark like a comma, or a space, as a separator.**/

public class UnderscoreInNumber {
    public static void main(String[] args) throws java.lang.Exception{

        int intNum = 1_00_00_000;
        System.out.println("intNum:" + intNum);

        long longNum = 1_00_00_000;
        System.out.println("longNum" + longNum);

        float floatNum = 2.10_001F;
        System.out.println("floatNum:" + floatNum);

        double doubleNum = 2.10_12_001;
        System.out.println("doubleNum:" + doubleNum);

        //OUTPUT
        //intNum: 10000000
        //longNum: 10000000
        //floatNum: 2.10001
        //doubleNum: 2.1012001
    }
}
