package Compiler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Stack;

import codegen.Code;
import codegen.SymboleTable;
import parser.Parser;
import scanner.DecafScanner;
import scanner.Token;

import static codegen.SymboleTable.symboleTables;

public class Compiler {

    public static DecafScanner scanner;
    public static ArrayList<Code> mipsCode;
    public static Stack<Integer> parseStack;
    public static Stack<Token> semanticStack; //change
    public static int address = 0;
    public static int temp = 0;
    public static int labelNum = 0;
    public static int maxTemp = 0;
    public static int stringAddr = 0;
    public static int falseNum = 0;
    public static ArrayList<String> strings;

    // t0 -> base of address
    // t1 -> base of temp
    // t2 -> base of double + 8
    // t9 -> base of strings + 64
    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        symboleTables = new ArrayList<>();
        symboleTables.add(new SymboleTable());
        scanner = new DecafScanner(new FileReader("src/chum.txt"));
        parseStack = new Stack<>();
        mipsCode = new ArrayList<>();
        semanticStack = new Stack<>();
        strings = new ArrayList<>();
        Parser parser = new Parser(scanner, "src/table.npt", true);
        parser.parse();
        mipsWriter();
    }

    public static void mipsWriter() throws IOException {
        File input = new File("./src/ans.s");
        FileWriter fileWriter = new FileWriter(input);
        fileWriter.write(".text\n");
        fileWriter.write(".globl main\n");
        for (int i = 0; i < mipsCode.size(); i++) {
            fileWriter.write(mipsCode.get(i).toString());
        }
        fileWriter.write(".data\n");
        fileWriter.write("    address: .space " + address+"\n");
        fileWriter.write("    temp: .space " + maxTemp+"\n");
        fileWriter.write("    buffer: .space " + 64+"\n");
        for (int i = 0; i < strings.size(); i++) {
            fileWriter.write("str" + i + ": .asciiz " + strings.get(i)+"\n");
        }
        fileWriter.write("nl: .asciiz \"\\n\"");
        fileWriter.flush();
    }

}
