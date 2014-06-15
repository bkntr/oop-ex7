package oop.ex7.main;

import java.io.File;

/**
 * Created by nman on 6/13/14.
 */
public class Sjavac {
    public static void main(String[] args[]){
        File sourceFile = new File(args[0]);
        boolean returnValue = Compiler.isCodeLegal(sourceFile);
    }
}
