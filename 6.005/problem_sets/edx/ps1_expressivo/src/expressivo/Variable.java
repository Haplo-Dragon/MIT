package expressivo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable implements Expression {
    private final String name;
    private final double coefficient;
    private final int power;

    private final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z]+");
    private final Pattern COEFFICIENT_PATTERN = Pattern.compile("[0-9]*(\\.[0-9]*)?");
    private final Pattern POWER_PATTERN = Pattern.compile("\\^[0-9]*");

    public Variable(String name, double coefficient, int power) {
        this.name = name;
        this.coefficient = coefficient;
        this.power = power;
    }

    public Variable(String input) {
        this.name = getName(input);
        this.coefficient = getCoefficient(input);
        this.power = getPower(input);
    }

    private String getName(String input) {
        return match(input, NAME_PATTERN);
    }

    private double getCoefficient(String input) {
        double coeff;

        try {
            coeff = Double.valueOf(match(input, COEFFICIENT_PATTERN));
        } catch (IllegalArgumentException e) {
            coeff = 1.0;
        }

        return coeff;
    }

    private int getPower(String input) {
        int pow;

        try {
            // This will return the exponent with its operator, e.g. "^10" for "x^10".
            final String exponent = match(input, POWER_PATTERN);
            // Now we'll grab just the numeric bit.
            pow = Integer.valueOf(exponent.substring(1));
        } catch (IllegalArgumentException e) {
            pow = 1;
        }

        return pow;
    }

    private String match(String input, Pattern pattern) {
        final Matcher m = pattern.matcher(input);

        if (m.find()) {
            return m.group(0);
        } else {
            throw new IllegalArgumentException(
                    "Variables must have non-negative double coefficients and non-empty " +
                            "names consisting of alphabetic characters." + input);
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        // Zero times anything is just zero.
        if (this.coefficient == 0) {
            return "0";
        }

        // We'll only add coefficients that aren't 1, because "1x" is ugly and redundant.
        if ((this.coefficient != 1) || (this.power == 0)) {
            // If the coefficient is exact (i.e., 3.0), we'll just append the integer
            // portion (i.e., we'll append "3").
            if (this.coefficient == (int) this.coefficient) {
                result.append((int) this.coefficient);
            } else {
                result.append(this.coefficient);
            }
        }

        // If the power is not zero, then we'll need the variable's name.
        if (this.power != 0) {
            result.append(this.name);
        }

        if (this.power > 1) {
            result.append("^").append(this.power);
        }

        return result.toString();
    }

    @Override
    public boolean isEmpty(){
        return false;
    }

    @Override
    public boolean equals(Object that){
        if (!(that instanceof Variable)) return false;

        final Variable var_that = (Variable) that;
        return ((this.name.equals(var_that.name)) &&
                (this.coefficient == var_that.coefficient) &&
                (this.power == var_that.power));
    }

    @Override
    public int hashCode() {
        return 31 *
                this.name.hashCode() +
                Double.hashCode(this.coefficient) +
                Integer.hashCode(this.power);
    }

}
