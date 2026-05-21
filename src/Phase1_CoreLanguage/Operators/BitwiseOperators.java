package Phase1_CoreLanguage.Operators;

/**
 * Bitwise and Shift Operators
 * ---------------------------
 * These operate on the individual BITS of integer types (byte, short, int, long,
 * char). Useful for low-level work: flags, masks, hashing, graphics, networking.
 * <p>
 *
 *      &       AND
 *      |       OR
 *      ^       XOR
 *      ~       NOT (one's complement) - flips every bit
 *      <<      left shift  (multiply by 2)
 *      >>      signed right shift  (divide by 2, keeps sign bit)
 *      >>>     unsigned right shift (fills high bit with 0)
 * <p>
 *
 * Why Use Them?
 * -------------
 *  - Compact flag sets (bitmask):  permissions = READ | WRITE;
 *  - Power-of-two multiplication:  x << 3  ==  x * 8
 *  - Quick parity check         :  (x & 1) == 0
 *  - Toggling bits              :  flags ^= MASK
 * <p>
 *
 * >> vs >>>  (Important!)
 * -----------------------
 * - >>  preserves the sign bit (so -8 >> 1 == -4).
 * - >>> shifts in zeros from the left (so -8 >>> 1 is a huge positive number).
 * <p>
 *
 * Both Java's int and long are signed. There is no unsigned int. The >>> operator
 * is the workaround when you want to treat the bit pattern as unsigned.
 */

public class BitwiseOperators {

    public static void main(String[] args) {
        int a = 0b1100;     // 12
        int b = 0b1010;     // 10

        System.out.println("a       = " + binary(a));
        System.out.println("b       = " + binary(b));
        System.out.println("a & b   = " + binary(a & b) + "  (= " + (a & b) + ")");
        System.out.println("a | b   = " + binary(a | b) + "  (= " + (a | b) + ")");
        System.out.println("a ^ b   = " + binary(a ^ b) + "  (= " + (a ^ b) + ")");
        System.out.println("~a      = " + binary(~a)   + "  (= " + (~a)   + ")");

        // --- Shifts ---
        int n = 5;
        System.out.println("5 << 1 = " + (n << 1));    // 10  (multiply by 2)
        System.out.println("5 << 3 = " + (n << 3));    // 40  (multiply by 8)
        System.out.println("20 >> 1= " + (20 >> 1));   // 10  (divide by 2)
        System.out.println("20 >> 2= " + (20 >> 2));   // 5

        // --- Signed vs unsigned right shift ---
        int negative = -8;
        System.out.println("-8 >>  1 = " + (negative >> 1));    // -4 (keeps sign)
        System.out.println("-8 >>> 1 = " + (negative >>> 1));   // huge positive number

        // --- Practical: bitmask flags ---
        final int READ    = 1 << 0;   // 0001
        final int WRITE   = 1 << 1;   // 0010
        final int EXECUTE = 1 << 2;   // 0100

        int perms = READ | WRITE;     // 0011

        System.out.println("Has READ?    " + ((perms & READ)    != 0));
        System.out.println("Has WRITE?   " + ((perms & WRITE)   != 0));
        System.out.println("Has EXECUTE? " + ((perms & EXECUTE) != 0));

        // Toggle EXECUTE on, then off
        perms ^= EXECUTE;
        System.out.println("After XOR, has EXECUTE? " + ((perms & EXECUTE) != 0));
        perms ^= EXECUTE;
        System.out.println("After XOR, has EXECUTE? " + ((perms & EXECUTE) != 0));

        // OUTPUT
        // a       = 0000000000000000000000000000_1100
        // b       = 0000000000000000000000000000_1010
        // a & b   = 0000000000000000000000000000_1000  (= 8)
        // a | b   = 0000000000000000000000000000_1110  (= 14)
        // a ^ b   = 0000000000000000000000000000_0110  (= 6)
        // ~a      = 1111111111111111111111111111_0011  (= -13)
        // 5 << 1 = 10
        // 5 << 3 = 40
        // 20 >> 1= 10
        // 20 >> 2= 5
        // -8 >>  1 = -4
        // -8 >>> 1 = 2147483644
        // Has READ?    true
        // Has WRITE?   true
        // Has EXECUTE? false
        // After XOR, has EXECUTE? true
        // After XOR, has EXECUTE? false
    }

    /** Pad to 32 bits and underscore-group for readability. */
    private static String binary(int value) {
        String raw = Integer.toBinaryString(value);
        raw = String.format("%32s", raw).replace(' ', '0');
        return raw.substring(0, 28) + "_" + raw.substring(28);
    }
}
