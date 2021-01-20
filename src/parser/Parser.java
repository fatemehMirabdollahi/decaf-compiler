package parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import scanner.DecafScanner;
import scanner.Token;

public class Parser {

    static DecafScanner scanner;
    static ArrayList<ArrayList<ParseCell>> parseTable;
    static Stack<Integer> parseStack;
    public static void main(String[] args) throws IOException {

        scanner = new DecafScanner(new FileReader("src/chum.txt"));
        parseStack = new Stack<>();

        createParseTable();

        while (true) {
            Token token = scanner.tokenReader();






        }
    }


    public static void createParseTable(){
//todo
    }
}
