package oop.ex7.scopes;

import oop.ex7.expressions.*;

/**
 * Created by nman on 6/13/14.
 */
public class MethodScope extends Scope {
    private MethodExpression method;
    private boolean returned = false;

    public MethodScope(MethodExpression method) {
        this.method = method;
    }

    public void doReturn(Expression expression)
            throws UninitializedVariableException, IncompatibleTypeException {
        if (expression.getType() == ExpressionType.UNKNOWN) {
            Variable returnVariable = getVariable(expression);

            if (returnVariable.getType() !=  method.getType()) {
                throw new IncompatibleTypeException();
            }

            if (!returnVariable.isInitialized()) {
                throw new UninitializedVariableException();
            }
        } else {
            if (expression.getType() != method.getType()) {
                throw new IncompatibleTypeException();
            }
        }

        returned = true;
    }

    @Override
    public void close() {
        if (!returned) {

        }

        super.close();
    }
}

