package codegen.discriptors;

import codegen.SymboleTable;
import scanner.Token;

import java.util.ArrayList;
import java.util.Stack;

public class FuncDscp extends Dscp{
    public String   returnType;
    public ArrayList<Token> inputNames;
    public SymboleTable symboleTable;
    public int size=4;
    public boolean hasReturn =false;
    public FuncDscp(String  returnType) {
        super(DscpType.funcion,-1);
        this.returnType =returnType;
        inputNames = new ArrayList<>();
        symboleTable = new SymboleTable();
    }
}
