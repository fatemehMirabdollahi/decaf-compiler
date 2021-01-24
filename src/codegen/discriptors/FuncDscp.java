package codegen.discriptors;

import codegen.SymboleTable;

import java.util.ArrayList;

public class FuncDscp extends Dscp{
    public ArrayList<String> inputTypes;
    public int returnAddress;
    public SymboleTable symboleTable;
    public FuncDscp(int addr) {
        super(DscpType.funcion, addr);
        this.symboleTable = new SymboleTable();
    }



}
