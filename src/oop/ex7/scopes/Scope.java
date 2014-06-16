package oop.ex7.scopes;

import java.util.Dictionary;

/**
 * Created by nman on 6/13/14.
 */
public class Scope {
    Scope parent;
    boolean closed = false;
    Dictionary<String, Variable> variables;

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
        // check if variable exists in variables

        // if so, check if it's of the same type as the value

        // if the variable was not initialized, initialize
    }

    public void callMethod(String methodName, String[] methodParameters,
                           String assignedVariable) {
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

    public void returnMethod(String returnValue) {
    }

    public void addBlock(String condition) {
    }

    public void setArrayValue(String name, String index,
                              Variable variableValue) {
    }

}

    