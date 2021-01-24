package codegen.discriptors;

import static codegen.discriptors.DscpType.variable;

public class VariableDscp extends Dscp {
    public VarType type;
    public String value;
    public boolean isImm;
    public boolean isTemp;

    public VariableDscp(VarType type, int addr , boolean isImm, boolean isTemp) {
        super(variable, addr);
        this.type = type;
        this.isImm = isImm;
        this.isTemp = isTemp;
    }


}
