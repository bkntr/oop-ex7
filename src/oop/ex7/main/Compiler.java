package oop.ex7.main;

import com.sun.org.apache.xpath.internal.operations.Variable;
import oop.ex7.scopes.BlockScope;
import oop.ex7.scopes.GlobalScope;
import oop.ex7.scopes.MethodScope;
import oop.ex7.scopes.Scope;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nman on 6/13/14.
 */
public class Compiler {

    private static final String DEFINE_METHOD_PATTERN =
            "[\\s]*(?<type>\\w*)(?<array>\\[\\])?[\\s]*(?<name>[A-Za-z]" +
                    "\\w*)[\\s]*\\(?<params>(.*)\\)[\\s]*[{][\\s]*";
    private static final String DEFINE_VARIABLE_PATTERN =
            "[\\s]*(?<type>\\w*)(?<array>\\[\\])?[\\s]*(?<name>[A-Za-z]" +
                    "\\w*)[\\s]*=?(?<value>.*);";
    private static final String DEFINE_BLOCK_PATTERN =
            "[\\s]*(if|while)[\\s]*\\(?<condition>(.*)\\)[\\s]*[{][\\s]*";
    private static final String CALL_METHOD_PATTERN =
           "[\\s]*((.*)=)*[\\s]*([A-Za-z]\\w*)[\\s]*\\((.*)\\)[\\s]*;[\\s]*"; //TODO
    private static final String SET_VALUE_PATTERN =
            "[\\s]*(?<name>\\w*)(\\[(?<index>.*)\\])?[\\s]*=(?<value>.*);";
    private static final String CLOSE_SCOPE_PATTERN = ".*}.*";
    private static final String RETURN_PATTERN =
            "[\\s]*return[\\s]*(?<value>.*);[\\s]*";
    /**
     * the function reads lines from code file.
     * check they have basic legal syntax,
     * classifies the lines to different expressions
     * @param sourceFile
     * @return
     */
    public static boolean isCodeLegal(File sourceFile)
            throws FileNotFoundException {
        Scanner scanner = new Scanner(sourceFile);
        Scope currentScope = new GlobalScope();
        String nextLine;

        while (scanner.hasNext()){
            nextLine = scanner.next();

            if (!isLineLegal(nextLine)){
                throw SemekException();
            }

            MethodScope method = handleDefineMethod(nextLine, currentScope);
            if (method != null){
                currentScope = method;
                continue;
            }

            BlockScope block = handleDefineBlock(nextLine, currentScope);
            if (block != null){
                currentScope = block;
                continue;
            }

            if (handleDefineVariable(nextLine, currentScope)){
                continue;
            }

            if (handleCallMethod(nextLine, currentScope)){
                continue;
            }

            if (handleSetValue(nextLine, currentScope)){
                continue;
            }

            if (handleReturn(nextLine, currentScope)){
                continue;
            }

            if (handleCloseScope(nextLine, currentScope)){
                continue;
            }
        }

        GlobalScope.checkFunctions();

        if (!GlobalScope.instance.isClosed()){
            throw SKLKException;
        }

        // code is legal
        return true;
    }

    private static boolean handleSetValue(String nextLine,
                                          Scope currentScope) {
        Matcher matcher = Pattern.compile(SET_VALUE_PATTERN).matcher(nextLine);

        if (!matcher.matches()){
            return false;
        }

        // extract variable name and new value
        String name = matcher.group("name");
        String value = matcher.group("value");
        String index = matcher.group("index");

        Variable variableValue = Expressions.ExpressionCreator.
                createExpression(value);

        if (index != null){
            currentScope.setArrayValue(name, index, variableValue);
        } else {
            currentScope.setValue(name, variableValue);
        }

        return true;
    }

    private static boolean handleCallMethod(String nextLine,
                                            Scope currentScope) {
        Matcher matcher = Pattern.compile(CALL_METHOD_PATTERN)
                .matcher(nextLine);

        if (!matcher.matches()){
            return false;
        }

        Expression method = Expression.createExpression(nextLine);

        // check method call is legal
        currentScope.callMethod(method);

        return true;
    }

    private static boolean handleDefineVariable(String nextLine,
                                                Scope currentScope) {
        Matcher matcher = Pattern.compile(DEFINE_VARIABLE_PATTERN).
                matcher(nextLine);

        if (!matcher.matches()){
            return false;
        }

        // extract variable type, name, value and is array
        String type = matcher.group("type");
        String name = matcher.group("name");
        String value = matcher.group("value");

        // create new variable from the relevant type and add
        // it to the current scope
        Variable variable =
                VariableFactory.createVariable(type, name);
        currentScope.addVariable(variable);


        // if while defining a variable the line also sets a
        // value, update it
        else if (value != null){
            Expression value = Expression.createExpression(value);
            currentScope.setValue(name, value);
        }

        return true;

    }

    private static BlockScope handleDefineBlock(String nextLine,
                                                Scope currentScope) {
        // cannot create blocks inside the general scope
        if (currentScope == GlobalScope.instance()){
            throw new BadFormatException();
        }

        Matcher matcher = Pattern.compile(DEFINE_BLOCK_PATTERN).
                matcher(nextLine);

        if (!matcher.matches()){
            return null;
        }

        // extract condition
        String condition = matcher.group("condition");

        // create expression of condition
        Expression condition =
                Expressions.ExpressionCreator.createExpression(condition);

        // create new scope of current block
        BlockScope block = new BlockScope(condition);

        // add block to scope
        currentScope.addBlock(block);

        return block;
    }

    private static MethodScope handleDefineMethod(String nextLine,
                                                  Scope currentScope) {
        // cannot create methods outside the general scope
        if (currentScope != GlobalScope.instance()){
            throw new BadFormatException();
        }

        Matcher matcher = Pattern.compile(DEFINE_METHOD_PATTERN).
                matcher(nextLine);

        if (!matcher.matches()){
            return null;
        }

        // extract method name, return value type and parameters
        String name = matcher.group("name");
        String returnValueType = matcher.group("type");
        String parameters = matcher.group("params");
        String[] parametersSplit = parameters.replaceAll(" ","").split(",");

        // create variables of the parameters
        //TODO

        // create variable of the return value type
        Variable methodReturnValueType =
                Expressions.VariableFactory.createVariable("returnValue",
                                                        returnValueType);

        // create new scope of the current method
        MethodScope method = new MethodScope(name, methodReturnValueType,
                parametersSplit);

        // add method to current scope
        currentScope.addMethod(method);

        return method;
    }

    /**
     * Each s-java line ends with `;', `{' or `}'.
     * Empty lines or line that start with '//' are legal but should be
     * ignored.
     * The function checks that the given line has a legal s-java syntax and
     * returns the line should be handled or ignored.
     * @param line a line of code to be checked
     * @return true if the line should be handled or false if it should be
     * ignored.
     * @throws illegalLineException if the line has illegal syntax
     */
    private static boolean isLineLegal(String line)
            throws illegalLineException{
        // check if the line starts with '//' or have only spaces
        if (line.matches("//.*|\\s*")){
            return false;
        // check if the line ends with ';','{','}'
        } else if (line.matches(".*[;|{|}]\\s*")){
            return true;
        }

        // if line doesn't match any of the two patterns, it is illegal
        throw new illegalLineException();

    }

    private static boolean handleReturn(String nextLine, Scope currentScope) {
        Matcher matcher = Pattern.compile(RETURN_PATTERN).matcher(nextLine);

        if (!matcher.matches()) {
            return false;
        }

        // extract return value
        String returnValue = matcher.group("value");

        Expression returnExpression =
                ExpressionCreator.createExpression(returnValue);

        // update the method had returned in the scope
        currentScope.returnMethod(returnValue);

        return true;
    }

    private static boolean handleCloseScope(String nextLine,
                                            Scope currentScope) {
        if (nextLine.matches(CLOSE_SCOPE_PATTERN)){
            currentScope.close();
            currentScope = currentScope.getParent();

            return true;
        }

        return false;
    }

}
