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
import com.sun.corba.se.impl.oa.toa.TOA;
import scanner.DecafScanner;
import scanner.Token;
import scanner.TokenType;

public class Parser {

    public static DecafScanner scanner;
    public static ArrayList<ArrayList<ParseCell>> parseTable;
    public static ArrayList<String> parseTableHeader;
    public static ArrayList<Code> mipsCode;
    public static Stack<Integer> parseStack;
    public static Stack<Token> semanticStack; //change
    public static Token token;
    public static int pc = 0;
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        symboleTableInit();
        scanner = new DecafScanner(new FileReader("src/chum.txt"));
        parseStack = new Stack<>();
        mipsCode = new ArrayList<>();
        int curState = 0;
        createParseTable();

        while (true) {
            token = scanner.tokenReader();
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

    private static void symboleTableInit() {
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
