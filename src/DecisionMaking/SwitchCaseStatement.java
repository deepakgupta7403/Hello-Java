package DecisionMaking;

/**
 * The switch statement is a multi-way branch statement.
 * It provides an easy way to dispatch execution to different parts of code based on the value of the expression.
 * Basically, the expression can be byte, short, char, and int primitive data types.
 * Beginning with JDK7, it also works with enumerated types ( Enums in java), the String class and Wrapper classes.
 *
 * Syntax:
 // switch statement
 switch(expression)
 {
 // case statements
 // values must be of same type of expression
 case value1 :
 // Statements
 break; // break is optional

 case value2 :
 // Statements
 break; // break is optional

 // We can have any number of case statements
 // below is default statement, used when none of the cases is true.
 // No break is needed in the default case.
 default :
 // Statements
 }
 *
 * Duplicate case values are not allowed.
 * The value for a case must be the same data type as the variable in the switch.
 * The value for a case must be a constant or a literal.Variables are not allowed.
 * The break statement is used inside the switch to terminate a statement sequence.
 * The break statement is optional. If omitted, execution will continue on into the next case.
 * The default statement is optional, and can appear anywhere inside the switch block.
 * In case, if it is not at the end, then a break statement must be kept after the default statement to omit the execution of next case statement.
 **/

public class SwitchCaseStatement {
    public static void main(String args[])
    {
        int i = 9;
        switch (i)
        {
            case 0:
                System.out.println("i is zero.");
                break;
            case 1:
                System.out.println("i is one.");
                break;
            case 2:
                System.out.println("i is two.");
                break;
            default:
                System.out.println("i is greater than 2.");
        }
    }

    //OUTPUT
    //i is greater than 2.


    private void StatementWithIntType() {
        int day = 5;
        String dayString;

        // switch statement with int data type
        switch (day) {
            case 1:
                dayString = "Monday";
                break;
            case 2:
                dayString = "Tuesday";
                break;
            case 3:
                dayString = "Wednesday";
                break;
            case 4:
                dayString = "Thursday";
                break;
            case 5:
                dayString = "Friday";
                break;
            case 6:
                dayString = "Saturday";
                break;
            case 7:
                dayString = "Sunday";
                break;
            default:
                dayString = "Invalid day";
                break;
        }
        System.out.println(dayString);


        //OUTPUT
        //Friday
    }



    /**Omitting the break statement
     *
     * As break statement is optional.
     * If we omit the break, execution will continue on into the next case.
     * It is sometimes desirable to have multiple cases without break statements between them.
     * For example, consider the updated version of above program, it also displays whether a day is a weekday or a weekend day.**/

    private void omittingOfBreakState() {
        {
            int day = 2;
            String dayType;
            String dayString;

            switch (day) {
                case 1:
                    dayString = "Monday";
                    break;
                case 2:
                    dayString = "Tuesday";
                    break;
                case 3:
                    dayString = "Wednesday";
                    break;
                case 4:
                    dayString = "Thursday";
                    break;
                case 5:
                    dayString = "Friday";
                    break;
                case 6:
                    dayString = "Saturday";
                    break;
                case 7:
                    dayString = "Sunday";
                    break;
                default:
                    dayString = "Invalid day";
            }

            switch (day) {
                // multiple cases without break statements

                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    dayType = "Weekday";
                    break;
                case 6:
                case 7:
                    dayType = "Weekend";
                    break;

                default:
                    dayType = "Invalid daytype";
            }

            System.out.println(dayString + " is a " + dayType);
        }
    }



    //OUTPUT
    //Tuesday is a Weekday





    /**Nested Switch Case statements
     *
     * We can use a switch as part of the statement sequence of an outer switch. This is called a nested switch.
     * Since a switch statement defines its own block, no conflicts arise between the case constants in the inner switch and those in the outer switch.
     *
     * **/



    private void nestedSwitchStatement() {
        String Branch = "CSE";
        int year = 2;

        switch (year) {
            case 1:
                System.out.println("elective courses : Advance english, Algebra");
                break;
            case 2:
                switch (Branch) // nested switch
                {
                    case "CSE":
                    case "CCE":
                        System.out.println("elective courses : Machine Learning, Big Data");
                        break;

                    case "ECE":
                        System.out.println("elective courses : Antenna Engineering");
                        break;

                    default:
                        System.out.println("Elective courses : Optimization");
                }
        }



        //OUTPUT
        //lective courses : Machine Learning, Big Data
    }
}