package expressivo;

public class Number implements Expression {
    private final int n;
    private final double double_n;

    public Number(int n) {
        this.n = n;
        this.double_n = 0;
    }

    public Number(double n) {
        this.n = 0;
        this.double_n = n;
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
