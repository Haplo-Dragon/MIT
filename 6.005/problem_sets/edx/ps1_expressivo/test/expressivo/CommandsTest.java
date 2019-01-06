/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

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
}
