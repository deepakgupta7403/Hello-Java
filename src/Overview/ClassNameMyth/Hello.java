package Overview.ClassNameMyth;

/**The Final Conclusion
 * If the class is public and file name are same as class name then it's working fine.
 * If the class is public and file name are different as class name then it's throw the error class name and file name should be same
 * If the class is not public and file name are same as class name then it's working fine.
 * All the program is did below**/

/*public class Hello {
    public static void main(String[] args) {
        System.out.println("In This case Class Name and File name are Same.");

        // OUTPUT
        // In This case Class Name and File name are Same.
    }
}*/


/*public class Overview.ClassNameMyth.HelloWorld {
    public static void main(String[] args) {
        System.out.println("In This case Class Name and File name are different.");

        // OUTPUT
        // Error:(13, 8) java: class Overview.ClassNameMyth.HelloWorld is public, should be declared in a file named Overview.ClassNameMyth.HelloWorld.java
    }
}*/


class HelloWorld {
    public static void main(String[] args) {
        System.out.println("In This case Class Name and File name are different but the class is not public.");

        // OUTPUT
        // In This case Class Name and File name are Same but the class is not public.

    }
}
