package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class Expression {
    private String expression;
    private boolean isArray;
    private ExpressionType type = ExpressionType.UNKNOWN;

    public Expression(String expression) {
        this.expression = expression;
    }

    public Expression(String expression, ExpressionType type) {
        this(expression);
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public ExpressionType getType() {
        return type;
    }

    public void setType(ExpressionType type) {
        this.type = type;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }
}
