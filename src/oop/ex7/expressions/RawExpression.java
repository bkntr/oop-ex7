package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class RawExpression implements Expression {
    private String value;

    public RawExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
