package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public abstract class Variable implements Expression {
    private static final String NAME_PATTERN = "^[_a-zA-Z]\\w*$";
    private String name;
    private boolean initialized;

    public Variable(String name) throws BadNameException {
        if (name.matches(NAME_PATTERN)) {
            this.name = name;
        } else {
            throw new BadNameException();
        }
    }

    public String getName() {
        return name;
    }

    public abstract void setValue(String value) throws BadValueException;

    public void setValue(Variable value) throws IncompatibleTypeException {
        if (this.getClass().isInstance(value.getClass())) {
            initialize();
        } else {
            throw new IncompatibleTypeException();
        }
    }

    public void initialize() {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
