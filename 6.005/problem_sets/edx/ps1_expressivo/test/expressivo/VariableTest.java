package expressivo;

import lib6005.parser.UnableToParseException;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;


public class VariableTest {
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testVariable() {
        final Variable var_x = new Variable("x");
        final Variable var_big_x = new Variable("X", 1, 1);

        final Variable var_max = new Variable("Max", 1, Integer.MAX_VALUE);

        assertNotEquals(var_x, var_big_x);
        assertNotEquals(var_x.hashCode(), var_big_x.hashCode());

        final Variable var_regex = new Variable("12x");
        assertEquals("12x", var_regex.toString());

        final Variable var_decimal = new Variable("4.603zed");
        assertEquals("4.603zed", var_decimal.toString());

        final Variable var_exp_explicit = new Variable("x", 1.5, 19);
        assertEquals("1.5x^19", var_exp_explicit.toString());
        final Variable var_exp = new Variable("1.5x^19");

        assertEquals(var_exp_explicit, var_exp);
        assertEquals("1.5x^19", var_exp.toString());
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
}
