package Compiler;

import Scope.BlockScope;
import Scope.GlobalScope;
import Scope.MethodScope;
import jdk.nashorn.internal.ir.Block;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nman on 6/13/14.
 */
public class Compiler {

    /**
     * the function reads line from code file.
     * checks they have basic legal syntax,
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
                if (isDefineMethod(nextLine)){
                    currentScope = new MethodScope();

                } else if (isBlock(nextLine)){
                    currentScope = new BlockScope();

                } else if (isDefineVariable(nextLine)){

                } else if (isCallFunction(nextLine)){

                } else if (isSetValue(nextLine)){

                }
                if (isCloseScope(nextLine)){
                    currentScope.close();
                    currentScope = currentScope.getParent();
                }
            }
        }
    }

    private static boolean isSetValue(String nextLine) {
        Pattern SetValuePattern = Pattern.compile("[\s]*(\w*)[\s]*=(.*)");
        Matcher SetValueMatcher = SetValuePattern.matcher(nextLine);

        if (SetValueMatcher.matches()){
            return true;
        }
        return false;
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
        // check if the line starts end with ';','{','}'
        } else if (line.matches(".*[;|{|}]\\s*")){
            return true;
        }

        // if line doesn't match any of the two patterns, it is illegal
        throw new illegalLineException();

    }
}
