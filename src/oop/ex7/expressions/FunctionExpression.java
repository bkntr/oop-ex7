package oop.ex7.expressions;

import java.util.ArrayList;

/**
 * Created by Ben on 15/06/2014.
 */
public class FunctionExpression implements Expression {
    private String name;
    private Expression[] parameters;

    public FunctionExpression(String name, Expression[] parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public Expression[] getParameters() {
        return parameters;
    }
}
