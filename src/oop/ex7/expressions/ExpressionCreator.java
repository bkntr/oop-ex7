package oop.ex7.expressions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ben on 15/06/2014.
 */
public class ExpressionCreator {
    private static final String NEGATIVE_PREFIX = "-";
    private static final String CREATE_ARRAY_SUFFIX = "[]";
    private static final String ARRAY_PATTERN = "\\{(?<values>.*)\\}";
    private static final String OPERATOR_PATTERN =
            "(?<first>.*)[\\+\\-\\*/](?<second>.*)";
    private static final String FUNCTION_PATTERN =
            "(?<name>\\w+)\\s*\\((?<params>.*)\\)";

    public static Expression createExpression(String expression)
            throws BadNameException {
        Matcher matcher;

        matcher = Pattern.compile(FUNCTION_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new FunctionExpression(matcher.group("name"),
                    splitExpression(matcher.group("params")));
        }

        matcher = Pattern.compile(ARRAY_PATTERN).matcher(expression);
        if (matcher.matches()) {
            ArrayExpression arrayExpression = new ArrayExpression();
            arrayExpression.setExpressions(
                    splitExpression(matcher.group("values")));
            return arrayExpression;
        }

        matcher = Pattern.compile(OPERATOR_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new OperatorExpression(
                    createExpression(matcher.group("first")),
                    createExpression(matcher.group("second")));
        }

        if (expression.startsWith(NEGATIVE_PREFIX)) {
            return new DoubleVariable(expression.substring(1));
        }
    }

    public static Variable createVariable(String type, String name)
            throws BadNameException, UnknownTypeException {
        boolean array = false;

        if (type.endsWith(CREATE_ARRAY_SUFFIX)) {
            type = type.substring(0, type.lastIndexOf(CREATE_ARRAY_SUFFIX));
            array = true;
        }

        Variable variable;
        switch (type) {
            case "int":
                variable = new IntVariable(name);
                break;
            case "double":
                variable = new DoubleVariable(name);
                break;
            case "String":
                variable = new StringVariable(name);
                break;
            case "boolean":
                variable = new BooleanVariable(name);
                break;
            case "char":
                variable = new CharVariable(name);
                break;
            default:
                throw new UnknownTypeException();
        }

        if (array) {
            variable = new ArrayVariable(name, variable);
        }

        return variable;
    }

    private static Expression[] splitExpression(String expression)
            throws BadNameException {
        ArrayList<Expression> expArray = new ArrayList<>();
        String[] values = expression.split(",", -1);
        for (String value: values) {
            expArray.add(createExpression(value));
        }

        return expArray.toArray(new Expression[expArray.size()]);
    }
}
