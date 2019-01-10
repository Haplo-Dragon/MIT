package expressivo;

import java.util.Map;

public class Number implements Expression {
    private final int n;
    private final double double_n;

    public Number(int n) {
        this.n = n;
        this.double_n = 0;
    }

    public Number(double n) {
        // If the number is a perfect integer, we'll ditch the unnecessary decimal point.
        if (n == (int) n) {
            this.n = (int) n;
            this.double_n = 0;
        } else {
            this.n = 0;
            this.double_n = n;
        }
    }

    public Number(String n) {
        if (n.contains(".")) {
            this.n = 0;
            this.double_n = Double.valueOf(n);
        } else {
            this.n = Integer.valueOf(n);
            this.double_n = 0;
        }
    }

    @Override
    public double getValue(Map<String, Double> environment) {
        return this.value();
    }

    private double value(){
        if (this.n == 0) {
            return this.double_n;
        } else {
            return (double) this.n;
        }
    }

    @Override
    public String toString(){
        if (this.value() == (int) this.value()) {
            return String.valueOf((int) this.value());
        }
        return String.valueOf(this.value());
    }

    @Override
    public boolean isEmpty(){
        return false;
    }

    @Override
    public boolean hasValue(Map<String, Double> environment) {
        return true;
    }

    @Override
    public Expression differentiate(String variable) {
        return Expression.make(0);
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        return this;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Number)) return false;

        final Number num_that = (Number) that;
        return this.value() == num_that.value();
    }

    @Override
    public int hashCode(){
        return 31 *
                Double.hashCode(this.value());
    }
}
