package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class BooleanVariable extends Variable {
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    public BooleanVariable(String name) throws BadNameException {
        super(name);
    }

    @Override
    public void setValue(String value) throws BadValueException {
        if (value.equals(TRUE_VALUE) || value.equals(FALSE_VALUE)) {
            initialize();
        } else {
            throw new BadValueException();
        }
    }
}
