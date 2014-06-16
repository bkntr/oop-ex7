package oop.ex7.expressions;

/**
 * Created by Ben on 16/06/2014.
 */
public class MultipleExpression extends Expression {
    Expression[] expressions;

    public MultipleExpression(String expression,
                              Expression[] expressions,
                              ExpressionType type) {
        super(expression, type);
        this.expressions = expressions;
    }

    public MultipleExpression(String expression, Expression[] expressions) {
        this(expression, expressions, ExpressionType.UNKNOWN);
    }

    public MultipleExpression(Expression[] expressions) {
        this("", expressions);
    }

    public Expression[] getExpressions() {
        return expressions;
    }
}
