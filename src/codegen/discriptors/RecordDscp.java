package codegen.discriptors;

import codegen.SymboleTable;

public class RecordDscp extends Dscp{
    public SymboleTable symboleTable;

    public RecordDscp() {
        this.symboleTable = new SymboleTable();
        super.dscpType = DscpType.record;
    }
}
