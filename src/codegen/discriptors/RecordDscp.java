package codegen.discriptors;

import codegen.SymboleTable;

public class RecordDscp extends Dscp{
    public SymboleTable symboleTable;

    public RecordDscp(int addr) {
        super(DscpType.record, addr);
        this.symboleTable = new SymboleTable();
    }
}
