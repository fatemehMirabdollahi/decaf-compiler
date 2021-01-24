package codegen;

import codegen.discriptors.*;
import scanner.Token;
import scanner.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

import static codegen.CodeGen.cast;
import static parser.Parser.*;

public class SymboleTable extends HashMap<Token, Dscp> {

    public static ArrayList<SymboleTable> symboleTables;

    public void add(Token name, Dscp dscp) {
        if (this.containsKey(name)) {
            //error
        }
        this.put(name, dscp);
    }

    public static Dscp find(Token t) {
        if (t.getType() == TokenType.integer) {
            VariableDscp d = new VariableDscp(new VarType(Type.Integer), -1, true, false);
            d.value = t.getValue();
            return d;
        }
        if (t.getType() == TokenType.real) {

            for (int i = symboleTables.size() - 1; i >= 0; i--) {
                Dscp d = symboleTables.get(i).get(t.getValue());
                if (d != null)
                    return d;
            }
            VariableDscp d = new VariableDscp(new VarType(Type.Double), doubleAddr, true, false);
            doubleAddr += d.type.size;
            d.value = t.getValue();
            symboleTables.get(symboleTables.size() - 1).add(t, d);
            return d;
        }

        for (int i = symboleTables.size() - 1; i >= 0; i--) {
            Dscp d = symboleTables.get(i).get(t.getValue());
            if (d != null)
                return d;
        }

        //error
        return null;
    }

    public static VarType getType(Dscp d1, Dscp d2, String op) {

        switch (op) {
            case "Arith":

                if (d1.dscpType != DscpType.variable || d2.dscpType != DscpType.variable) {
                    //error
                } else {
                    VarType t1 = ((VariableDscp) d1).type;
                    VarType t2 = ((VariableDscp) d2).type;
                    if ((t1.type == Type.Integer && t2.type == Type.Integer) ||
                            (t1.type == Type.Integer && t2.type == Type.Boolean) ||
                            (t1.type == Type.Boolean && t2.type == Type.Integer)
                    ) {

                        return new VarType(Type.Integer);

                    } else if ((t1.type == Type.Integer && t2.type == Type.Double) ||
                            (t1.type == Type.Double && t2.type == Type.Integer) ||
                            (t1.type == Type.Double && t2.type == Type.Double) ||
                            (t1.type == Type.Double && t2.type == Type.Boolean) ||
                            (t1.type == Type.Boolean && t2.type == Type.Double)
                    ) {

                        return new VarType(Type.Double);

                    } else {
                        //error
                    }
                }
                break;
            case "equal":
                if (d1.dscpType != DscpType.variable || d2.dscpType != DscpType.variable) {
                    //error
                } else {
//                    String t1 = ((VariableDscp) d1).type;
//                    String t2 = ((VariableDscp) d2).type;

                }
        }
        return null;
    }

}
