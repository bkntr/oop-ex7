package oop.ex7.main;

import oop.ex7.CompilerException;
import oop.ex7.expressions.*;
import oop.ex7.scopes.*;

import java.io.*;
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
    public static void compile(File sourceFile)
            throws IOException, CompilerException {
        compileGlobalScope(sourceFile);
        compileInnerScopes(sourceFile);
    }

    private static void compileGlobalScope(File sourceFile)
            throws IOException, CompilerException {
        try (LineNumberReader reader =
                     new LineNumberReader(new FileReader(sourceFile))) {
            String line;
            GlobalScope scope = GlobalScope.instance();

            while ((line = reader.readLine()) != null) {
                // if line is a comment or spaces only, ignore it
                if (!handleLine(line)) {
                    continue;
                }

                int indent = 0;
                if (line.endsWith("{")) {
                    indent++;
                } else if (line.endsWith("}")) {
                    indent--;
                }

                // if the line is part of the global scope
                if (indent > 0) {
                    continue;
                }

                MethodExpression method = defineMethod(line);
                if (method != null) {
                    scope.addMethod(method);
                    continue;
                }

                if (handleDefineVariable(line, scope)) {
                    continue;
                }

                if (handleCallMethod(line, scope)) {
                    continue;
                }

                throw new BadFormatException();
            }
        }
    }

    private static void compileInnerScopes(File sourceFile)
            throws CompilerException, IOException {
        try (LineNumberReader reader =
                     new LineNumberReader(new FileReader(sourceFile))) {
            Scope currentScope = GlobalScope.instance();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!handleLine(line)) {
                    continue;
                }

                if (currentScope == GlobalScope.instance()) {
                    continue;
                }

                BlockScope block = handleDefineBlock(line, currentScope);
                if (block != null) {
                    currentScope = block;
                    continue;
                }

                if (handleDefineVariable(line, currentScope)) {
                    continue;
                }

                if (handleCallMethod(line, currentScope)) {
                    continue;
                }

                if (handleSetValue(line, currentScope)) {
                    continue;
                }

                if (handleReturn(line, currentScope)) {
                    continue;
                }

                if (handleCloseScope(line, currentScope)) {
                    currentScope = currentScope.getParent();
                    continue;
                }

                throw new BadFormatException();
            }
        }
    }


    private static boolean handleSetValue(String nextLine,
                                          Scope currentScope)
            throws CompilerException {
        Matcher matcher = Pattern.compile(SET_VALUE_PATTERN).matcher(nextLine);

        if (!matcher.matches()){
            return false;
        }

        // extract variable name and new value
        String name = matcher.group("name");
        String valueString = matcher.group("value");
        String indexString = matcher.group("index");

        Expression value = ExpressionCreator.createExpression(valueString);
        Expression index = ExpressionCreator.createExpression(indexString);

        if (index != null){
            currentScope.setArrayValue(name, index, value);
        } else {
            currentScope.setValue(name, value);
        }

        return true;
    }

    private static boolean handleCallMethod(String line, Scope currentScope)
            throws CompilerException {
        if (!line.matches(CALL_METHOD_PATTERN)){
            return false;
        }

        MethodExpression method =
                (MethodExpression)ExpressionCreator.createExpression(line);

        // check method call is legal
        currentScope.callMethod(method, null);

        return true;
    }

    private static boolean handleDefineVariable(String line,
                                                Scope currentScope)
            throws CompilerException {
        Matcher matcher = Pattern.compile(DEFINE_VARIABLE_PATTERN).
                matcher(line);

        if (!matcher.matches()){
            return false;
        }

        // extract variable type, name, value and is array
        String type = matcher.group("type");
        String name = matcher.group("name");
        String value = matcher.group("value");

        // create new variable from the relevant type and add
        // it to the current scope
        Variable variable = ExpressionCreator.createVariable(type, name);
        currentScope.addVariable(variable);

        // if while defining a variable the line also sets a
        // value, update it
        if (value != null){
            Expression valueExpression =
                    ExpressionCreator.createExpression(value);
            currentScope.setValue(name, valueExpression);
        }

        return true;

    }

    private static BlockScope handleDefineBlock(String nextLine,
                                                Scope currentScope)
            throws CompilerException {
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
        String conditionString = matcher.group("condition");

        // create expression of condition
        Expression condition =
                ExpressionCreator.createExpression(conditionString);

        // create new scope of current block
        BlockScope block = new BlockScope();

        // add block to scope
        currentScope.addBlock(condition);

        return block;
    }

    private static MethodExpression defineMethod(String line)
            throws CompilerException {
        if (!line.matches(DEFINE_METHOD_PATTERN)){
            return null;
        }

        return (MethodExpression)ExpressionCreator.createExpression(line);
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
     * @throws BadFormatException if the line has illegal syntax
     */
    private static boolean handleLine(String line)
            throws BadFormatException{
        // check if the line starts with '//' or have only spaces
        if (line.matches("//.*|\\s*")){
            return false;
        // check if the line ends with ';','{','}'
        } else if (line.matches(".*[;|{|}]\\s*")){
            return true;
        }

        // if line doesn't match any of the two patterns, it is illegal
        throw new BadFormatException();

    }

    private static boolean handleReturn(String nextLine, Scope currentScope)
            throws CompilerException {
        Matcher matcher = Pattern.compile(RETURN_PATTERN).matcher(nextLine);

        if (!matcher.matches()) {
            return false;
        }

        if (!(currentScope instanceof MethodScope)) {
            throw new BadFormatException();
        }

        // extract return value
        String returnValue = matcher.group("value");

        Expression returnExpression =
                ExpressionCreator.createExpression(returnValue);

        // update the method had returned in the scope
        ((MethodScope)currentScope).doReturn(returnExpression);

        return true;
    }

    private static boolean handleCloseScope(String nextLine,
                                            Scope currentScope) {
        if (nextLine.matches(CLOSE_SCOPE_PATTERN)){
            currentScope.close();
            return true;
        }

        return false;
    }

}
