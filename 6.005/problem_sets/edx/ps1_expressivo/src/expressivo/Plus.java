package expressivo;

public class Plus implements Expression {
    private final Expression left;
    private final Expression right;

    public Plus(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + this.left.toString() + " + " + this.right.toString() + ")";
    }

    @Override
    public boolean isEmpty(){
        return false;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Plus)) return false;

        final Plus plus_that = (Plus) that;
        return ((this.left == plus_that.left) &&
                (this.right == plus_that.right));
    }

    @Override
    public int hashCode() {
        return 31 *
                this.left.hashCode() +
                this.right.hashCode();
    }
}
