package codegen;

import codegen.discriptors.*;
import scanner.Token;
import scanner.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

import static codegen.discriptors.Dscp.typeSetter;
import static parser.Parser.*;

public class SymboleTable extends HashMap<Token, Dscp> {

    public static ArrayList<SymboleTable> symboleTables;
    public Token name;

    public void add(Token name, Dscp dscp) {
        if (this.containsKey(name)) {
            //error
        }
        this.put(name, dscp);
    }


    public static Dscp find(Token t) {

        if (t.getType() == TokenType.undefined) {
            //error
        }
        if (t.getType() == TokenType.integer) {
            VariableDscp d = new VariableDscp(new VarType(Type.Integer), -2, true, false);
            d.value = t.getValue();
            return d;
        }
        if (t.getType() == TokenType.real) {
            VariableDscp d = new VariableDscp(new VarType(Type.Double), -2, true, false);
            d.value = t.getValue();
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

            case "Compare":

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

    public static void castImmtoBool(Token token, String src) {
        int be = 0;
        switch (token.getType()) {
            case keyword:
                //error
                break;
            case real:
                if (Double.parseDouble(token.getValue()) > 0) be = 1;
                else be = 0;
                mipsCode.add(new Code("li.d", src, be + ".0"));

                break;

            case integer:
                if (Integer.parseInt(token.getValue()) > 0) be = 1;
                else be = 0;
                mipsCode.add(new Code("li", src, String.valueOf(be)));
                break;

            case str_char:
                if (token.getValue().length() > 2)
                    be = 1;
                else be = 0;
                mipsCode.add(new Code("li", src, String.valueOf(be)));

        }
    }

    public static void castIntToDouble(VariableDscp d, String base, String dest) {
        if (d.isImm) {
            mipsCode.add(new Code("li", dest, d.value));
        } else {
            mipsCode.add(new Code("l.d", dest, d.addr + base));
        }
        mipsCode.add(new Code("cvt.d.w", dest, dest));
    }

    public static void castDoubleToInt(VariableDscp d, String base, String dest) {
        if (d.isImm) {
            mipsCode.add(new Code("li.d", dest, d.value + ".0"));
        } else {
            mipsCode.add(new Code("l.d", dest, d.addr + base));
            mipsCode.add(new Code("cvt.w.d", dest, dest));
        }

    }

    public static void castVariableToBool(VariableDscp d, String base, String dest, String src) {
        if (d.type.type == Type.Integer) {

            mipsCode.add(new Code("lw", src, d.addr + base));
            mipsCode.add(new Code("li", dest, "0"));
            mipsCode.add(new Code("beqz", src, "false:"));
            mipsCode.add(new Code("li", dest, "1"));
            mipsCode.add(new Code("label", "false:"));

        } else if (d.type.type == Type.Double) {
            mipsCode.add(new Code("ld", src, d.addr + base));
            mipsCode.add(new Code("li", dest, "0"));
            mipsCode.add(new Code("li", "$f10", "0"));
            mipsCode.add(new Code("c.eq.d", "$f10", src));
            mipsCode.add(new Code("bc1t", "false:"));
            mipsCode.add(new Code("li", dest, "1"));
            mipsCode.add(new Code("label", "false:"));
        }
    }

}
