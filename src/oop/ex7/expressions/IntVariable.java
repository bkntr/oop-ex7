package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class IntVariable extends NumberVariable {
    public IntVariable(String name) throws BadNameException {
        super(name);
    }

    @Override
    public void setValue(String value) throws BadValueException {
        try {
            Integer.parseInt(value);
            initialize();
        } catch (NumberFormatException e) {
            throw new BadValueException();
        }
    }
}
