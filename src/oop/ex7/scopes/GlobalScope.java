package oop.ex7.scopes;

import oop.ex7.expressions.Expression;
import oop.ex7.expressions.ExpressionType;
import oop.ex7.expressions.MethodExpression;
import oop.ex7.expressions.Variable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nman on 6/13/14.
 */
public class GlobalScope extends Scope {
    private static GlobalScope instance;

    private HashMap<String, MethodExpression> methods = new HashMap<>();

    private GlobalScope() { }

    public void addMethod(MethodExpression method) {
        methods.put(method.getName(), method);
    }

    public MethodExpression getMethod(String name) {
        return methods.get(name);
    }

    public static GlobalScope instance() {
        if (instance == null) {
            instance = new GlobalScope();
        }

        return instance;
    }

    public Variable getVariable(String name) {
        Variable variable = variables.get(name);

        // check if variable exists in variables
        if (variable == null){
            throw noSuchVariable();
        }
    }
}
