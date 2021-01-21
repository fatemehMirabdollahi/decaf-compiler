package codegen.discriptors;

import java.util.ArrayList;

public class ArrayDscp extends Dscp{
    public String type;
    public String size;
    public ArrayList<String> elemnts;

    public ArrayDscp(String type) {
        this.type = type;
        elemnts = new ArrayList<>();
        super.dscpType = DscpType.array;
    }
}
