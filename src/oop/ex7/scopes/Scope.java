package oop.ex7.scopes;

import oop.ex7.expressions.Expression;
import oop.ex7.expressions.FunctionExpression;
import oop.ex7.expressions.IncompatibleTypeException;
import oop.ex7.expressions.Variable;
import sun.tools.tree.MethodExpression;

import java.util.HashMap;

/**
 * Created by nman on 6/13/14.
 */
public class Scope {
    Scope parent;
    boolean closed = false;
    HashMap<String, Variable> variables;

    public void close() {
        closed = true;
    }

    public boolean isClosed(){
        return closed;
    }

    public Scope getParent() {
        return parent;
    }

    public void setValue(String variableName, Variable variableValue) {
        Variable variable = variables.get(variableName);

        // check if variable exists in variables
        if (variable == null){
            this.parent.setValue(variableName, variableValue);
        }

        // check the value type
        ExpressionType type = variableValue.getType();

        // if value is a variable, check if it exists and of a good type
        if (type == UNKNOWN){
            Variable variableExists = variables.get(variableValue.getName());
            if (variableExists == null){
                throw NoSuchVariableException();
            }

            // set value into variable
            variable.setValue(variableExists);
            return;
        }

        // if it is a multiple expression, check it
        if (variableValue instanceof MultipleExpression){
            if (variableValue.isArray() && !variable.isArray()){
              throw Bexception;
            }
            ExpressionType variableType = variable.getType();
            Expression[] expressions = variableValue.getExpressions();
            for (Expression expression: expressions){
                if (expression.getType() != variableType){
                    throw new IncompatibleTypeException();
                }
            }
        }

        // set the value
        variable.setValue(variableValue);
    }

    public void setArrayValue(String name, Expression index,
                              Expression variableValue) {
        // check index is valid
        if (index.getType() != INT){
            throw illegalIndexException();
        }

        setValue(name, variableValue);

    }

    public void callMethod(MethodExpression method,
                           Expression assignedVariable) {
        // check that the method exists
        MethodExpression definedMethod =
                GlobalScope.instance().getMethod(method.getName());

        if (definedMethod == null){
            throw noSuchMethodException();
        }

        Expression[] parameters = method.getParameters();

        // check if parameters exist and initialized
        for (Expression parameter: parameters){
            // parameters with type other than unknown have been recognized
            // in inner scopes
            if (parameter.getType() == UNKNOWN){
                Expression parameterVariable =
                        variables.get(parameter.getExpression());
                boolean isInitialized = parameterVariable.isInitialized();
                // if they are, update to correct type
                if ((parameterVariable != null) && (isInitialized)){
                    parameter.setType(parameterVariable.getType());
                }
            }
        }

        // if not, pass to parent to check if they exist there
        this.parent.callMethod(method, assignedVariable);

        // check the assigned variable is of the same type as return value

    }

    /**
     * checks if the variables dictionary doesn't have a variable in that
     * name. if so, add it to the variables dictionary
     * @param variable variable to add
     */
    public void addVariable(Variable variable) {
        String variableName = variable.getName();

        // check if the variable already exists, if not add it
        if (variables.get(variableName) != null){
            throw SomeException();
        }

        variables.put(variableName, variable);
    }

    public void addBlock(Expression condition) {
        ExpressionType type = condition.getType();

        if (type == UNKNOWN){
            Expression variable = variables.get(condition.getExpression());
            if (variable == null){
                this.parent.addBlock(condition);
            } else if (!variable.isInitialized()){
                throw blablaException();
            }
        } else if (type != BOOLEAN){
            throw badConditionExpression();
        }
    }
}

    