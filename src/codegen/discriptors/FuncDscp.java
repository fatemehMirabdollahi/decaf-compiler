package codegen.discriptors;

import codegen.SymboleTable;

import java.util.ArrayList;

public class FuncDscp extends Dscp{
    public ArrayList<String> inputTypes;
    public int beginAddress;
    public int returnAddress;
    public SymboleTable symboleTable;

    public FuncDscp(int beginAddress) {
        this.beginAddress = beginAddress;
        this.symboleTable = new SymboleTable();
        super.dscpType = DscpType.funcion;
    }



}
