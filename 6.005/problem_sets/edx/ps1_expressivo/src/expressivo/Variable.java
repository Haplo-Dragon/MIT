package expressivo;

public class Variable implements Expression {
    private final String name;
    private final double coefficient;
    private final int power;

    public Variable(String name, double coefficient, int power) {
        this.name = name;
        this.coefficient = coefficient;
        this.power = power;
    }

    public Variable(String name) {
        this(name, 1, 1);
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
