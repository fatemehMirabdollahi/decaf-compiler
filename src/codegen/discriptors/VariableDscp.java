package codegen.discriptors;

public class VariableDscp extends Dscp {
    public String type;
    public String value;

    public VariableDscp(String type) {
        this.type = type;
        super.dscpType = DscpType.variable;
    }


}
