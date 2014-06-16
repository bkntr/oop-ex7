package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class StringVariable extends Variable {
    private static final String STRING_PATTERN = "\".*\"";

    public StringVariable(String name) throws BadNameException {
        super(name);
    }

    @Override
    public void setValue(String value) throws BadValueException {
        if (value.matches(STRING_PATTERN)) {
            initialize();
        } else {
            throw new BadValueException();
        }
    }
}
