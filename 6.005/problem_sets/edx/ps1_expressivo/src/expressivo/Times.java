package expressivo;

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
    public boolean equals(Object that) {
        if (!(that instanceof Times)) return false;

        final Times times_that = (Times) that;
        return ((this.left == times_that.left) &&
                (this.right == times_that.right));
    }

    @Override
    public int hashCode() {
        return 31 *
                this.left.hashCode() +
                this.right.hashCode();
    }
}
