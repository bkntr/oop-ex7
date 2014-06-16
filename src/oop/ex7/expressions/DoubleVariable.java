package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class DoubleVariable extends IntVariable {
    public DoubleVariable(String name) throws BadNameException {
        super(name);
    }

    @Override
    public void setValue(String value) throws BadValueException {
        try {
            Double.parseDouble(value);
            initialize();
        } catch (NumberFormatException e) {
            throw new BadValueException();
        }
    }
}
