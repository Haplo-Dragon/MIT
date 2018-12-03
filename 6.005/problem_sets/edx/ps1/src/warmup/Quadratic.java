package warmup;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.lang.Math;

public class Quadratic {

    /**
     * Find the integer roots of a quadratic equation, ax^2 + bx + c = 0.
     * @param a coefficient of x^2
     * @param b coefficient of x
     * @param c constant term.  Requires that a, b, and c are not ALL zero.
     * @return all integers x such that ax^2 + bx + c = 0.
     */
    static Set<Integer> roots(int a, int b, int c) {
        assert ( ! (a == 0 && b == 0 && c == 0)) : "All coefficients cannot be zero.";

        Set<Integer> roots = new HashSet<>();

        // We might not have gotten an actual quadratic equation.
        if (a == 0) {
            final double single_root = (double) ((0 - c) / b);
            // If the root is an integer, we'll add it to the set.
            if ((int) single_root == single_root) {
                roots.add((int) single_root);
            }
        }

        // We're using BigInteger here in case the value of a coefficient overflows.
        // I have a nasty suspicion that this is INCREDIBLY slow, but I'm not sure how
        // else to handle integer overflow. It's also incredibly verbose.
        final BigInteger big_a = BigInteger.valueOf(a);
        final BigInteger big_b = BigInteger.valueOf(b);
        final BigInteger big_c = BigInteger.valueOf(c);

        final BigInteger b_squared = big_b.pow(2);
        final BigInteger four_ac = big_a.multiply(big_c)
                .multiply(BigInteger.valueOf(4));

        // b^2 - 4ac
        BigInteger plus_or_minus = b_squared.subtract(four_ac);

        final double root_term = Math.sqrt(plus_or_minus.doubleValue());
        final boolean is_perfect_square = (int) root_term == root_term;

        // If a is zero, the roots will be undefined. Similarly, if plus_or_minus
        // is negative, the roots will be complex numbers. If the square root of
        // plus_or_minus is not an integer (i.e., it's not a perfect square), the roots
        // will be real numbers.
        // We're ONLY calculating integer roots, so in any of these cases, we'll skip the
        // rest of the calculations and return the empty set.
        if ((a != 0) && (plus_or_minus.signum() != -1) && is_perfect_square){

            // sqrt(b^2 - 4ac)
            plus_or_minus = BigInteger.valueOf(Math.round(
                    Math.sqrt(plus_or_minus.doubleValue())));

            // -b + sqrt(b^2 - 4ac)
            final BigInteger numerator_plus = plus_or_minus.add(big_b.negate());
            // -b - sqrt(b^2 - 4ac)
            final BigInteger numerator_minus = plus_or_minus.negate().subtract(big_b);

            // Divide by 2*a
            final double quotient_plus = numerator_plus.doubleValue() / (2 * a);
            final double quotient_minus = numerator_minus.doubleValue() / (2 * a);

            // And now we'll convert back to integers because that's what the spec requires
            // us to return.
            final int root_plus = (int) quotient_plus;
            final int root_minus = (int) quotient_minus;

            // If the roots are integers, we'll add them to the set.
            if (root_plus == quotient_plus) {
                roots.add(root_plus);
            }
            if (root_minus == quotient_minus) {
                roots.add(root_minus);
            }

        }

        return roots;
    }

    
    /**
     * Main function of program.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("For the equation x^2 - 4x + 3 = 0, the possible solutions are:");
        Set<Integer> result = roots(1, -4, 3);
        System.out.println(result);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
