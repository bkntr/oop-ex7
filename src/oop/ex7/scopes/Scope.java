package oop.ex7.scopes;

import oop.ex7.expressions.*;

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

    public void setValue(String variableName, Expression value) throws IncompatibleTypeException {
        // check if variable exists in variables
        Variable variable = getVariable(variableName);

        // if value is a variable, check if it exists and of a good type
        if (value.getType() == ExpressionType.UNKNOWN){
            Variable valueVariable = getVariable(value.getExpression());
            if (valueVariable == null){
                throw new UnresolvedSymbolException();
            }

            // set value into variable
            variable.setValue(valueVariable);
            return;
        }

        // if it is a multiple expression, check it
        if (value instanceof MultipleExpression){
            if (value.isArray() && !variable.isArray()){
              throw Bexception;
            }
            ExpressionType variableType = variable.getType();
            Expression[] expressions = value.getExpressions();
            for (Expression expression: expressions){
                if (expression.getType() != variableType){
                    throw new IncompatibleTypeException();
                }
            }
        }

        // set the value
        variable.setValue(value);
    }

    public void setArrayValue(String name, Expression index,
                              Expression variableValue) {
        // check index is valid
        if (index.getType() != ExpressionType.INT){
            throw illegalIndexException();
        }

        setValue(name, variableValue);

    }

    public void callMethod(MethodExpression method,
                           Expression returnExpression) throws IncompatibleTypeException {
        // check that the method exists
        MethodExpression definedMethod =
                GlobalScope.instance().getMethod(method.getName());

        if (definedMethod == null){
            throw noSuchMethodException();
        }

        // check that the assigned value e
        Variable returnVariable = getVariable(returnExpression);

        // check the assigned variable is of the same type as return value
        if (returnVariable.getType() != definedMethod.getType()) {
            throw IncompatibleTypeException();
        }

        Expression[] givenParameters = method.getParameters();
        Expression[] definedParameters = definedMethod.getParameters();

        if (givenParameters.length != definedParameters.length){
            throw differentNumOfParams();
        }

        // check if parameters exist, initialized and of the right type
        for (int i = 0; i < givenParameters.length; i++){
            Expression givenParameter = givenParameters[i];
            Variable definedParameter = definedParameters[i];

            // check if given parameter is a variable
            ExpressionType type = givenParameter.getType();
            if (type == ExpressionType.UNKNOWN) {
                Variable givenParameterVariable =
                        getVariable(givenParameter.getExpression());
                type = givenParameterVariable.getType();
                // check if the variable is initialized
                if (!givenParameterVariable.isInitialized()) {
                    throw IllegalParam();
                }
            }
            // check if the given parameter is of the right type
            if (type != definedParameter.getType()){
                throw new IncompatibleTypeException();
            }
        }
    }

    public Variable getVariable(String name) {
        Variable variable = variables.get(name);

        // check if variable exists in variables
        if (variable == null){
            return this.parent.getVariable(name);
        }

        return variable;
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

        if (type == ExpressionType.UNKNOWN){
            Variable variable = variables.get(condition.getExpression());
            if (variable == null){
                this.parent.addBlock(condition);
            } else if (!variable.isInitialized()){
                throw blablaException();
            }
        } else if (type != ExpressionType.BOOLEAN){
            throw badConditionExpression();
        }
    }
}

    