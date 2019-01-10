package expressivo;

import java.util.Collections;
import java.util.Map;

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
    public boolean hasValue(Map<String, Double> environment) {
        return false;
    }

    @Override
    public double getValue(Map<String, Double> environment) {
        return 0;
    }

    @Override
    public Expression differentiate(String variable) {
        return new EmptyExpression();
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
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
