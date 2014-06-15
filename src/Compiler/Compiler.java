package Compiler;

import Scope.BlockScope;
import Scope.GlobalScope;
import Scope.MethodScope;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nman on 6/13/14.
 */
public class Compiler {

    private static final String DEFINE_METHOD_PATTERN = "[\\s]*(\\w)(\\[\\])?[\\s]*([A-Za-z]\\w*)[\\s]*\\((.*)\\)[\\s]*[{][\\s]*\n";
    private static final String DEFINE_VARIABLE_PATTERN = "[\\s]*(\\w)(\\[\\])?[\\s]*([A-Za-z]\\w*)[\\s]*=?(.*)\n";
    private static final String DEFINE_BLOCK_PATTERN = "[\\s]*(if|while)[\\s]*\\(.*\\)[\\s]*[{][\\s]*\n";
    private static final String CALL_METHOD_PATTERN = "[\\s]*((.*)=)*[\\s]*([A-Za-z]\\w*)[\\s]*\\((.*)\\)[\\s]*;[\\s]*\n";
    private static final String SET_VALUE_PATTERN = "[\\s]*(\\w*)[\\s]*=(.*)\n";
    private static final String CLOSE_SCOPE_PATTERN = ".*}.*\n";

    /**
     * the function reads lines from code file.
     * check they have basic legal syntax,
     * classifies the lines to different expressions
     * @param sourceFile
     * @return
     */
    public static boolean isCodeLegal(File sourceFile) {
        Scanner scanner = new Scanner(sourceFile);
        Scope currentScope = new GlobalScope();
        String nextLine;

        while (scanner.hasNext()){
            nextLine = scanner.next();

            if (isLineLegal(nextLine)){
                if (nextLine.matches(DEFINE_METHOD_PATTERN){
                    currentScope = new MethodScope();

                } else if (nextLine.matches(DEFINE_BLOCK_PATTERN){
                    currentScope = new BlockScope();

                } else if (nextLine.matches(DEFINE_VARIABLE_PATTERN)){
                    

                } else if (nextLine.matches(CALL_METHOD_PATTERN){

                } else if (nextLine.matches(SET_VALUE_PATTERN)){

                } else if (nextLine.matches(CLOSE_SCOPE_PATTERN)){
                    currentScope.close();
                    currentScope = currentScope.getParent();
                }
            }
        }
    }

    /**
     * Each s-java line ends with `;', `{' or `}'.
     * Empty lines or line that start with '//' are legal but should be ignored.
     * The function checks that the given line has a legal s-java syntax and
     * returns the line should be handled or ignored.
     * @param line a line of code to be checked
     * @return true if the line should be handled or false if it should be ignored.
     * @throws illegalLineException if the line has illegal syntax
     */
    private static boolean isLineLegal(String line) throws illegalLineException{
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
}
