package parser;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

import codegen.Code;
import codegen.CodeGen;
import codegen.SymboleTable;
import scanner.DecafScanner;
import scanner.Token;
import scanner.TokenType;

public class Parser {

    static DecafScanner scanner;
    static ArrayList<ArrayList<ParseCell>> parseTable;
    static Stack<Integer> parseStack;
    static ArrayList<String> parseTableHeader;
    static ArrayList<SymboleTable> symboleTables;
    static int pc = 0;
    static ArrayList<Code> mipsCode;
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        scanner = new DecafScanner(new FileReader("src/chum.txt"));
        parseStack = new Stack<>();
        mipsCode = new ArrayList<>();
        int curState = 0;
        createParseTable();

        while (true) {
            Token token = scanner.tokenReader();
            if(token.getType() == TokenType.undefined){
                //scanner error
            }
            ParseCell curCell = getCell(token, curState);
            if (curCell == null){
                //parser error
            } else {

                //do action
                doAction(curCell);

                //invoke code gen
                Method method = CodeGen.class.getMethod(curCell.getSemantic(),null);
                method.setAccessible(true);
                method.invoke(null, null);
            }
        }
    }

    static ParseCell getCell(Token token, int state){
        return null;
    }
    static void createParseTable(){
//todo
    }
    static void doAction(ParseCell p){

    }
}
