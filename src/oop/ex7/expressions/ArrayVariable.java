package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class ArrayVariable extends Variable {
    private Variable baseVariable;
    public ArrayVariable(String name, Variable baseVariable) throws BadNameException {
        super(name);
        this.baseVariable = baseVariable;
    }

    @Override
    public void setValue(String value) throws BadValueException {
        baseVariable.setValue(value);
    }

    @Override
    public void setValue(Variable value) throws IncompatibleTypeException {
        baseVariable.setValue(value);
    }
}
