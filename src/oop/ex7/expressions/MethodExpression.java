package oop.ex7.expressions;

import java.util.ArrayList;

/**
 * Created by Ben on 15/06/2014.
 */
public class MethodExpression extends MultipleExpression {

    public MethodExpression(String expression,
                            Expression[] expressions,
                            ExpressionType type) {
        super(expression, expressions, type);
    }

    public MethodExpression(String name, Expression[] parameters) {
        super(name, parameters);
    }

    public String getName() {
        return getExpression();
    }

    public Expression[] getParameters() {
        return expressions;
    }
}
