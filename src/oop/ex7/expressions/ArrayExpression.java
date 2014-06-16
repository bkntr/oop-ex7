package oop.ex7.expressions;

import java.util.ArrayList;

/**
 * Created by Ben on 15/06/2014.
 */
public class ArrayExpression implements Expression {
    private Expression[] expressions;

    public void setExpressions(Expression[] expressions) {
        this.expressions = expressions;
    }

    public Expression[] getExpressions() {
        return expressions;
    }
}
