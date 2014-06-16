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
            "(?<first>.*)\\s*[\\+\\-\\*/]\\s*(?<second>.*)";
    private static final String DEFINE_METHOD_PATTERN =
            "(?<type>\\w+)\\s*(?<name>\\w+)\\s*\\((?<params>.*)\\)";
    private static final String CALL_METHOD_PATTERN =
            "(?<name>\\w+)\\s*\\((?<params>.*)\\)";
    private static final String NUMBER_PATTERN =
            "\\-?\\d+(<?decimal>\\.\\d+)?";
    private static final String STRING_PATTERN = "\".*\"";
    private static final String CHAR_PATTERN = "'.'";
    private static final String BOOLEAN_PATTERN = "(true|false)";
    private static final String TYPED_EXPRESSION_PATTERN =
            "(?<type>\\w+)\\s+(?<name>\\w+)";

    public static Expression createExpression(String expression)
            throws BadNameException, UnknownTypeException {
        Matcher matcher;

        matcher = Pattern.compile(DEFINE_METHOD_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new MethodExpression(matcher.group("name"),
                    splitExpression(matcher.group("params")),
                    parseType(matcher.group("type")));
        }

        matcher = Pattern.compile(CALL_METHOD_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new MethodExpression(matcher.group("name"),
                    splitExpression(matcher.group("params")));
        }

        matcher =
                Pattern.compile(TYPED_EXPRESSION_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new Expression(matcher.group("name"),
                    parseType(matcher.group("type")));
        }

        matcher = Pattern.compile(ARRAY_PATTERN).matcher(expression);
        if (matcher.matches()) {
            MultipleExpression array = new MultipleExpression(
                    splitExpression(matcher.group("values")));
            array.setArray(true);
            return array;
        }

        matcher = Pattern.compile(OPERATOR_PATTERN).matcher(expression);
        if (matcher.matches()) {
            Expression[] expressions = new Expression[] {
                    createExpression(matcher.group("first")),
                    createExpression(matcher.group("second")) };
            return new MultipleExpression(expressions);
        }

        matcher = Pattern.compile(NUMBER_PATTERN).matcher(expression);
        if (matcher.matches()) {
            if (matcher.group("decimal") == null) {
                return new Expression(expression, ExpressionType.INT);
            } else {
                return new Expression(expression, ExpressionType.DOUBLE);
            }
        }

        matcher = Pattern.compile(STRING_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new Expression(expression, ExpressionType.STRING);
        }

        matcher = Pattern.compile(CHAR_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new Expression(expression, ExpressionType.CHAR);
        }

        matcher = Pattern.compile(BOOLEAN_PATTERN).matcher(expression);
        if (matcher.matches()) {
            return new Expression(expression, ExpressionType.BOOLEAN);
        }

        if (expression.startsWith(NEGATIVE_PREFIX)) {
            return new Expression(expression, ExpressionType.INT);
        }

        return new Expression(expression);
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

    private static ExpressionType parseType(String type)
            throws UnknownTypeException {
        switch (type) {
            case "int":
                return ExpressionType.INT;
                break;
            case "double":
                return ExpressionType.DOUBLE;
                break;
            case "String":
                return ExpressionType.STRING;
                break;
            case "boolean":
                return ExpressionType.BOOLEAN;
                break;
            case "char":
                return ExpressionType.CHAR;
                break;
            default:
                throw new UnknownTypeException();
        }
    }

    public static Variable createVariable(String type, String name)
            throws BadNameException, UnknownTypeException {

        Variable variable = new Variable(name);
        if (type.endsWith(CREATE_ARRAY_SUFFIX)) {
            type = type.substring(0, type.lastIndexOf(CREATE_ARRAY_SUFFIX));
            variable.setArray(true);
        }

        variable.setType(parseType(type));
        return variable;
    }
}
