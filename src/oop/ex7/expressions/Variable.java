package oop.ex7.expressions;

/**
 * Created by Ben on 15/06/2014.
 */
public class Variable extends Expression {
    private static final String NAME_PATTERN = "^[_a-zA-Z]\\w*$";
    private boolean initialized;

    public Variable(String name, ExpressionType type) throws BadNameException {
        super(name, type);

        if (!name.matches(NAME_PATTERN)) {
            throw new BadNameException();
        }
    }

    public Variable(String name) throws BadNameException {
        this(name, ExpressionType.UNKNOWN);
    }

    public String getName() {
        return getExpression();
    }

    public void setValue(Expression value) throws IncompatibleTypeException {
        if (this.getType().equals(value.getType())) {
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
