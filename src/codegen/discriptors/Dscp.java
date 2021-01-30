package codegen.discriptors;

import scanner.Token;
import scanner.TokenType;

import static codegen.SymboleTable.symboleTables;


public abstract class Dscp {
    public DscpType dscpType;
    public int addr;

    public Dscp(DscpType dscpType, int addr) {
        this.dscpType = dscpType;
        this.addr = addr;
    }

    public static VarType typeSetter(String type) throws Exception {
        if(type == null)
            return null;
        VarType v;
        switch (type) {
            case "int":
                v = new VarType(Type.Integer);
                break;
            case "double":
                v = new VarType(Type.Double);
                break;

            case "string":
                v = new VarType(Type.String);
                break;

            case "bool":
                v = new VarType(Type.Boolean);
                break;

            default:
                v = new VarType(Type.Record);
                Token t = new Token(type, TokenType.id);
                Dscp d = symboleTables.get(0).get(t);
                if(d == null) {
                    System.out.println("not declared record");
                    //error
                    throw new Exception();
                }
        }
        return v;

    }


}
