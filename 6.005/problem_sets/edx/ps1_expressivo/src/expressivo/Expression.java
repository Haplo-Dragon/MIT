package expressivo;

import lib6005.parser.*;
import org.junit.platform.commons.util.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS1 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition:
    //      Expression =    Number(n: int or double) +
    //                      Variable(name: String, coefficient: String, power: int) +
    //                      Plus(left: Expression, right: Expression) +
    //                      Times(left: Expression, right: Expression)
    //
    // Operations:
    //      Creators:
    //          EmptyExpression: creates empty expression
    //

    final String GRAMMAR_FILE_PATH = "src/expressivo/Expression.g";

    enum ExpressionGrammar {
        ROOT,
        SUM,
        PRODUCT,
        PRIMITIVE,
        NUMBER,
        VARIABLE,
        WHITESPACE
    }

    public static Expression empty() {
        return new EmptyExpression();
    }

    public static Expression make(int n) {
        return new Number(n);
    }

    public static Expression make(double n) {
        return new Number(n);
    }

    public static Expression make(String name, double coefficient, int power) {
        return new Variable(name, coefficient, power);
    }

    public static Expression plus(Expression left, Expression right) {
        if (left.isEmpty()) {
            return right;
        }

        if (right.isEmpty()) {
            return left;
        }

        return new Plus(left, right);
    }

    public static Expression times(Expression left, Expression right) {
        if (left.isEmpty()) {
            return right;
        }

        if (right.isEmpty()) {
            return left;
        }

        return new Times(left, right);
    }

    public boolean isEmpty();

    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS1 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) throws IllegalArgumentException {
        Expression result = Expression.empty();

        try {
            Parser<ExpressionGrammar> parser =
                    GrammarCompiler.compile(
                            new File(GRAMMAR_FILE_PATH), ExpressionGrammar.ROOT);
            ParseTree<ExpressionGrammar> tree = parser.parse(input);
            // Uncomment the line below to display the generated tree in a browser window.
            //tree.display();

            result = buildAST(tree);

        } catch (UnableToParseException | IOException e) {
            //e.printStackTrace();
            throw new IllegalArgumentException("Unable to parse expression.");
        }

        return result;
    }

    static Expression buildAST(ParseTree<ExpressionGrammar> tree) {
        switch(tree.getName()) {
            case NUMBER:
                return new Number(tree.getContents());

            case VARIABLE:
                return new Variable(tree.getContents());

            // A primitive has a number, a variable, a sum, or a product as a child.
            case PRIMITIVE:
                // This primitive has a number as a child.
                if (!(tree.childrenByName(ExpressionGrammar.NUMBER).isEmpty())) {
                    return buildAST(tree.childrenByName(ExpressionGrammar.NUMBER).get(0));

                // This primitive has a variable as a child.
                } else if (!(tree.childrenByName(ExpressionGrammar.VARIABLE).isEmpty())) {
                    return buildAST(tree.childrenByName(ExpressionGrammar.VARIABLE).get(0));

                // This primitive has a sum as a child.
                } else if (!(tree.childrenByName(ExpressionGrammar.SUM).isEmpty())) {
                    return buildAST(tree.childrenByName(ExpressionGrammar.SUM).get(0));

                // This primitive has a product as a child.
                } else {
                    return buildAST(tree.childrenByName(ExpressionGrammar.PRODUCT).get(0));
                }

            // A sum will have one or more children that must be summed together. We only
            // need to sum the children that are primitives.
            case SUM:
                boolean first_sum = true;
                Expression result_sum = Expression.empty();

                for (ParseTree<ExpressionGrammar> child :
                        tree.childrenByName(ExpressionGrammar.PRODUCT)) {
                    if (first_sum) {
                        result_sum = buildAST(child);
                        first_sum = false;
                    } else {
                        result_sum = new Plus(result_sum, buildAST(child));
                    }
                }

                if (first_sum) {
                    throw new RuntimeException(
                            "Sum must have a non-whitespace child." + tree);
                }

                return result_sum;

            // A product is similar to a sum. It has one or more children that must be
            // multiplied together. Again, we only need to multiply children that are
            // primitive.
            case PRODUCT:
                boolean first_product = true;
                Expression result_product = Expression.empty();

                for (ParseTree<ExpressionGrammar> child :
                        tree.childrenByName(ExpressionGrammar.PRIMITIVE)) {
                    if (first_product) {
                        result_product = buildAST(child);
                        first_product = false;
                    } else {
                        result_product = new Times(result_product, buildAST(child));
                    }
                }

                if (first_product) {
                    throw new RuntimeException(
                            "Sum must have a non-whitespace child." + tree);
                }

                return result_product;

            // The root has either a single sum child or a single product child.
            case ROOT:
                // The root has a sum child.
                if (!(tree.childrenByName(ExpressionGrammar.SUM).isEmpty())) {
                    return buildAST(tree.childrenByName(ExpressionGrammar.SUM).get(0));

                // The root has a product child.
                } else {
                    return buildAST(tree.childrenByName(ExpressionGrammar.PRODUCT).get(0));
                }

            // We never call buildAST with whitespace, so we should never reach here.
            case WHITESPACE:
                throw new RuntimeException("Attempting to parse whitespace, you should " +
                        "never reach here." + tree);

        }
        throw new RuntimeException("Past end of switch statement while parsing, you" +
                " should never reach here." + tree);
    }
    
    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS1 handout.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}
