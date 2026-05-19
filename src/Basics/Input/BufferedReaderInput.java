package Basics.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Taking Input - java.io.BufferedReader
 * -------------------------------------
 * BufferedReader is the older (pre-Java-5) way to read input. It reads one whole
 * LINE at a time as a String - you have to parse the result yourself.
 *
 *
 * Why use BufferedReader over Scanner?
 * ------------------------------------
 * - Faster than Scanner because it does not do regex-based tokenizing.
 *   Commonly used in competitive programming for that reason.
 * - Larger buffer size by default (8192 chars vs Scanner's 1024).
 *
 *
 * Trade-offs
 * ----------
 * - Returns Strings only. You must convert with Integer.parseInt(),
 *   Double.parseDouble(), etc.
 * - Throws checked IOException, so you must declare "throws" or wrap in try/catch.
 * - No built-in support for reading individual tokens of different types from the
 *   same line (you would split the line yourself).
 */

public class BufferedReaderInput {

    public static void main(String[] args) throws IOException {
        // System.in is a byte stream. We wrap it with InputStreamReader to convert
        // bytes -> chars, and then with BufferedReader to read line-by-line efficiently.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // 1) Read a full line as a String
        System.out.print("Enter your full name: ");
        String name = br.readLine();

        // 2) Read a number - need explicit parsing
        System.out.print("Enter your age      : ");
        int age = Integer.parseInt(br.readLine());

        // 3) Read multiple tokens from one line - split manually
        System.out.print("Enter three numbers separated by spaces: ");
        String[] tokens = br.readLine().split("\\s+");
        int a = Integer.parseInt(tokens[0]);
        int b = Integer.parseInt(tokens[1]);
        int c = Integer.parseInt(tokens[2]);

        System.out.println();
        System.out.println("Hello " + name + ", age " + age);
        System.out.println("Sum of the three numbers = " + (a + b + c));

        br.close();

        // SAMPLE RUN
        // Enter your full name: Deepak Gupta
        // Enter your age      : 25
        // Enter three numbers separated by spaces: 10 20 30
        //
        // Hello Deepak Gupta, age 25
        // Sum of the three numbers = 60
    }
}
