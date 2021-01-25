package codegen.discriptors;

import scanner.Token;

import java.util.ArrayList;

public class ArrayDscp extends Dscp{
    public VarType type;
    public String size;


    public ArrayDscp(Token type) {
        super(DscpType.array, -1);

        this.type = typeSetter(type);

    }
}
