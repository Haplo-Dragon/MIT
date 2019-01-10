/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;


import lib6005.parser.UnableToParseException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void testPlus() {
        final Expression three_x_squared = Expression.make("x", 3, 2);
        final Expression empty = Expression.empty();

        final Expression plus_empty = Expression.plus(three_x_squared, empty);

        assertEquals(three_x_squared, plus_empty);

        final Expression nums_only = Expression.plus(
                Expression.make(572), Expression.make(.75));

        final Expression multi = Expression.times(plus_empty, nums_only);

        assertEquals("(3x^2 * (572 + 0.75))", multi.toString());

        final Expression zero = Expression.make(0);
        assertEquals("0", zero.toString());

        final Expression twenty_five = Expression.make(25);
        final Expression plus_zero = Expression.plus(zero, twenty_five);
        assertEquals(twenty_five, plus_zero);
    }

    @Test
    public void testParse() {
        final Expression expr = Expression.parse("4 * 2x");

        final Expression staff = Expression.parse("3 + 2.4");
        assertTrue(staff.toString().contains("3 + 2.4"));

        final Expression staff_2 = Expression.parse("3 * x + 2.4");

        final Expression staff_3 = Expression.parse("3 * (x + 2.4)");

        final Expression staff_oops = Expression.parse("((3 + 4) * x * x)");
        final Expression staff_4 = Expression.parse("foo + bar+baz");
        final Expression staff_5 = Expression.parse("(2*x    )+    (    y*x    )");
        final Expression staff_6 = Expression.parse("4 + 3 * x + 2 * x * x + 1 * x * x * (((x)))");

        final Expression expr2 = Expression.parse("x*x + 10x^2 * 4.5y");

        assertThrows(IllegalArgumentException.class, () -> {
            final Expression invalid = Expression.parse("3 *");
        });
    }

    @Test
    public void testDifferentiate() {
        final Expression constant = Expression.parse("14");
        assertEquals(Expression.make(0), constant.differentiate("x"));

        final Expression one_term = Expression.parse("5x^3");
        final Expression one_term_diff = Expression.parse("15x^2");
        assertEquals(one_term_diff, one_term.differentiate("x"));

        final Expression orig = Expression.parse("4x^2 * 4.37 + 3y");
        final Expression diff_x = Expression.parse("4.37 * 8x");
        assertEquals(diff_x, orig.differentiate("x"));
    }

    @Test
    public void testEquality() {
        final Expression orig = Expression.parse("4 * 2x + 3");
        final Expression same = Expression.parse("(4) * (2x) + (3)");

        assertEquals(orig, same);
    }

    @Test
    public void testSimplifySingleVariable() {
        final Expression unsimplified = Expression.parse("x*x*x");
        final Expression simplified = Expression.parse("x^2 * x");

        assertEquals(simplified, unsimplified.simplify(Collections.EMPTY_MAP));

        final Map<String, Double> x_equals_3 = new HashMap<>();
        x_equals_3.put("x", 3.0);
        final Expression simple_x_3 = Expression.parse("27");

        assertEquals(simple_x_3, unsimplified.simplify(x_equals_3));

        final Expression order_of_ops = Expression.parse("3x^2");

        assertEquals(simple_x_3, order_of_ops.simplify(x_equals_3));
    }

    @Test
    public void testSimplifyMultiVariable() {
        final Expression unsimplified = Expression.parse("x*x*x + y*y*y");
        final Expression simplified = Expression.parse("x^2 * x + y^2 * y");

        assertEquals(simplified, unsimplified.simplify(Collections.EMPTY_MAP));

        final Map<String, Double> y_equals_10 = new HashMap<>();
        y_equals_10.put("y", 10.0);
        y_equals_10.put("z", 250.0);
        y_equals_10.put("var", 2.3);
        final Expression simple_y_10 = Expression.parse("x^2 * x + 1000");

        assertEquals(simple_y_10, unsimplified.simplify(y_equals_10));
    }

    @Test
    public void testSimplifyPlus() {
        final Expression unsimplified = Expression.parse("x + x + x");
        final Expression simplified = Expression.parse("2x + x");

        assertEquals(simplified, unsimplified.simplify(Collections.EMPTY_MAP));
    }

    @Test
    public void testSimplifyNumbersOnly() {
        final Expression plus = Expression.parse("1+2");
        final Expression simple = Expression.parse("3");

        assertEquals(simple, plus.simplify(Collections.EMPTY_MAP));

        final Expression unsimplified = Expression.parse("1+2*3+8*0.5");
        final Expression simplified = Expression.parse("11.0");

        assertEquals(simplified, unsimplified.simplify(Collections.EMPTY_MAP));
    }

}
