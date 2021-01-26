package codegen.discriptors;

public class VariableDscp extends Dscp {
    public VarType type;
    public String value;
    public boolean isImm;
    public boolean isTemp;
    public String refType;

    public VariableDscp(VarType type, int addr , boolean isImm, boolean isTemp) {
        super(DscpType.variable, addr);
        this.type = type;
        this.isImm = isImm;
        this.isTemp = isTemp;
    }

}
