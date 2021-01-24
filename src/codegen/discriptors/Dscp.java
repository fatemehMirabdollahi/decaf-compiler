package codegen.discriptors;

public abstract class Dscp {
    public DscpType dscpType;
    public int addr;

    public Dscp(DscpType dscpType, int addr) {
        this.dscpType = dscpType;
        this.addr = addr;
    }
}
