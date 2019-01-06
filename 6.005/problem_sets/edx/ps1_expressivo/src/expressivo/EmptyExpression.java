package expressivo;

public class EmptyExpression implements Expression {
    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean isEmpty(){
        return true;
    }

    @Override
    public Expression differentiate(String variable) {
        return new EmptyExpression();
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof EmptyExpression;
    }

    @Override
    public int hashCode() {
        return 31 * "".hashCode();
    }
}
