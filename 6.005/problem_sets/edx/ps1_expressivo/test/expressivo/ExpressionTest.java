/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;


import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Expression abstract data type.
 */
public class ExpressionTest {

    // Testing strategy:
    //
    // Partition input space as follows:
    //      Expression: empty, non-empty, with Plus, with Times, with variable, w/o variable
    //      Number: 1, 1 < MAX_INT, MAX_INT, 1 < MAX_DOUBLE, MAX_DOUBLE
    //      Variable:
    //          name: length == 1, >1, case sensitivity
    //          power: 0, 1, >1, MAX_INT

    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testEmptyExpressions() {
        final Expression expr = Expression.empty();
        final Expression expr2 = Expression.empty();

        assertEquals("", expr.toString());
        assertEquals(expr, expr2);

        assertEquals(31 * "".hashCode(), expr.hashCode());
    }

    @Test
    public void testNumber() {
        final Number num_int = new Number(4);
        final Number num_four = new Number(4.0);

        final Number num_double = new Number(Double.MAX_VALUE);
        final Number num_double2 = new Number(Double.MAX_VALUE);

        assertEquals(num_int, num_four);
        assertEquals(num_double, num_double2);
        assertNotEquals(num_int, num_double);

        assertEquals("4", num_int.toString());

        assertEquals(num_int.hashCode(), num_four.hashCode());
    }

    @Test
    public void testVariable() {
        final Variable var_x = new Variable("x");
        final Variable var_big_x = new Variable("X", 1, 1);

        final Variable var_max = new Variable("Max", 1, Integer.MAX_VALUE);

        assertNotEquals(var_x, var_big_x);
        assertNotEquals(var_x.hashCode(), var_big_x.hashCode());
    }


    @Test
    public void testVarToString() {
        final Variable x = new Variable("x");
        assertEquals("x", x.toString());

        final Variable x_squared = new Variable("x", 1, 2);
        assertEquals("x^2", x_squared.toString());

        final Variable x_to_zeroth_power = new Variable("x", 1, 0);
        assertEquals("1", x_to_zeroth_power.toString());

        final Expression y_thirteenth = Expression.make("y", 3.2, 13);
        assertEquals("3.2y^13", y_thirteenth.toString());

        final Expression zero_anything = Expression.make("var", 0, 4);
        assertEquals("0", zero_anything.toString());
    }

    @Test
    public void testPlus() {
        final Expression three_x_squared = Expression.make("x", 3, 2);
        final Expression empty = Expression.empty();

        final Expression plus_empty = Expression.plus(three_x_squared, empty);

        assertEquals(three_x_squared, plus_empty);

        final Expression nums_only = Expression.plus(
                Expression.make(572), Expression.make(.75));

        final Expression multi = Expression.times(plus_empty, nums_only);

        assertEquals("(3x^2 * (572 + 0.75))", multi.toString());
    }

}
