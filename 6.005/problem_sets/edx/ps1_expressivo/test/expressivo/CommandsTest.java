/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the static methods of Commands.
 */
public class CommandsTest {

    // Testing strategy

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testDifferentiate() {
        final String constant = "14";
        assertEquals("0", Commands.differentiate(constant, "x"));

        final String one_term = "5x^3";
        final String one_term_diff = "15x^2";
        assertEquals(one_term_diff, Commands.differentiate(one_term, "x"));

        final String orig = "4x^2 * 4.37 + 3y";
        final String diff_x = "((4.37 * 8x) + 3y)";
        assertEquals(diff_x, Commands.differentiate(orig, "x"));

    }

    @Test
    public void testSimplify() {
        final String x_cubed = "x*x*x";

        assertEquals("(x^2 * x)", Commands.simplify(x_cubed, Collections.EMPTY_MAP));

        final Map<String, Double> x_equals_3 = new HashMap<>();
        x_equals_3.put("x", 3.0);

        assertEquals("27", Commands.simplify(x_cubed, x_equals_3));

        final String x_and_y = "x*x*x + y*y*y";
        final String simplified_x_and_y = "((x^2 * x) + (y^2 * y))";

        assertEquals(simplified_x_and_y, Commands.simplify(
                x_and_y, Collections.EMPTY_MAP));

        final String nums = "1+2*3+8*0.5";

        assertEquals("11", Commands.simplify(nums, Collections.EMPTY_MAP));
    }
}
