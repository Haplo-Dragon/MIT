package expressivo;

import java.util.Map;

public class Times implements Expression{
    private final Expression left;
    private final Expression right;

    public Times(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + this.left.toString() + " * " + this.right.toString() + ")";
    }

    @Override
    public boolean isEmpty(){
        return false;
    }

    @Override
    public boolean hasValue(Map<String, Double> environment) {
        return (this.left.hasValue(environment) &&
                this.right.hasValue(environment));
    }

    @Override
    public double getValue(Map<String, Double> environment) {
        if (this.hasValue(environment)) {
            return this.left.getValue(environment) *
                   this.right.getValue(environment);
        } else {
            return 1;
        }
    }

    @Override
    public Expression differentiate(String variable) {
        // Derivative of left * right = left * right' + right * left'

        // This is the left term times the derivative of the right term.
        final Expression left_times_d_right =
                Expression.times(this.left, this.right.differentiate(variable));

        // This is the right term times the derivative of the left term.
        final Expression right_times_d_left =
                Expression.times(this.right, this.left.differentiate(variable));

        // And finally we'll add them together.
        return Expression.plus(left_times_d_right, right_times_d_left);
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        if (this.hasValue(environment)) {
            return new Number(this.getValue(environment));
        } else {
            return Expression.times(
                    this.left.simplify(environment),
                    this.right.simplify(environment));
        }
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Times)) return false;

        final Times times_that = (Times) that;
        return ((this.left.equals(times_that.left)) &&
                (this.right.equals(times_that.right)));
    }

    @Override
    public int hashCode() {
        return 31 *
                this.left.hashCode() +
                this.right.hashCode();
    }
}
