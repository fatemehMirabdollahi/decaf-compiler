package codegen;

import codegen.discriptors.*;
import scanner.DecafScanner;
import scanner.Token;
import scanner.TokenType;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Compiler.Compiler.*;


public class SymboleTable extends HashMap<Token, Dscp> {

    public static ArrayList<SymboleTable> symboleTables;
    public Token name;

    public void add(Token name, Dscp dscp) throws Exception {
        if (this.containsKey(name)) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("variable already exists");
            throw new Exception();
        }
        this.put(name, dscp);
    }


    public static Dscp find(Token t) throws Exception {

        if (t.getType() == TokenType.undefined) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("can't use return value of void function");
            throw new Exception();
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
            Dscp d = symboleTables.get(i).get(t);
            if (d != null)
                return d;
        }
        if (t.getType() == TokenType.str_char) {
            VariableDscp d = new VariableDscp(new VarType(Type.String), temp, true, false);
            mipsCode.add(new Code("la", "$t3", "str" + stringAddr));
            mipsCode.add(new Code("sw", "$t3", temp + "($t1)"));
            strings.add(t.getValue());
            d.stringAddress = stringAddr;
            stringAddr += 1;
            temp += 4;
            d.value = t.getValue();
            return d;
        }
        //error
        System.out.println("line " + scanner.lineNum + " :");
        System.out.println("reference not declared");
        throw new Exception();

    }

    public static VarType getType(Dscp d1, Dscp d2) throws Exception {


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
            //
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not proper type for arithmetic");
            throw new Exception();
        }


    }

    public static void castImmToBool(Token token, String src) throws Exception {
        int be = 0;
        switch (token.getType()) {

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
                break;
            default:
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("can't cast to boolean");
                throw new Exception();

        }
    }

    public static void castIntToDouble(VariableDscp d, String base, String dest) {
        if (d.isImm) {
            mipsCode.add(new Code("li.d", dest, d.value + ".0"));
        } else {
            mipsCode.add(new Code("l.d", dest, d.addr + base));
            mipsCode.add(new Code("cvt.d.w", dest, dest));
        }

    }

    public static void castDoubleToInt(VariableDscp d, String base, String dest) {
        if (d.isImm) {
            mipsCode.add(new Code("li", dest, String.valueOf((int) Double.parseDouble(d.value))));
        } else {
            mipsCode.add(new Code("l.d", "$f10", d.addr + base));
            mipsCode.add(new Code("cvt.w.d", "$f10", "$f10"));
            mipsCode.add(new Code("mfc1.d", dest, "$f10"));
        }

    }

    public static void castVariabelToBool(VariableDscp d, String base, String dest, String src) {
        if (d.type.type == Type.Integer) {

            mipsCode.add(new Code("lw", src, d.addr + base));
            mipsCode.add(new Code("li", dest, "0"));
            mipsCode.add(new Code("beqz", src, "false" + falseNum));
            mipsCode.add(new Code("li", dest, "1"));
            mipsCode.add(new Code("label", "false" + falseNum + ":"));
            falseNum ++;

        } else if (d.type.type == Type.Double) {
            mipsCode.add(new Code("l.d", src, d.addr + base));
            mipsCode.add(new Code("li", dest, "0"));
            mipsCode.add(new Code("li", "$f10", "0"));
            mipsCode.add(new Code("c.eq.d", "$f10", src));
            mipsCode.add(new Code("bc1t", "false" + falseNum));
            mipsCode.add(new Code("li", dest, "1"));
            mipsCode.add(new Code("label", "false" + falseNum + ":"));
            falseNum ++;
        }
    }

    public static void doubleCompare(String dest, String chum) {

        mipsCode.add(new Code("li", dest, "0"));
        mipsCode.add(new Code("bc1" + chum, "false" + falseNum));
        mipsCode.add(new Code("li", dest, "1"));
        mipsCode.add(new Code("label", "false" + falseNum + ":"));
        falseNum ++;
    }


    public static int storeNotTempInSP(SymboleTable symboleTable, int base) {
        int size = 0;
        for (Map.Entry<Token, Dscp> me : symboleTable.entrySet()) {
            Dscp dscp = me.getValue();
            if (dscp.dscpType == DscpType.variable) {
                VariableDscp variableDscp = (VariableDscp) dscp;
                if (!variableDscp.isTemp) {
                    String register;
                    if (variableDscp.type.type == Type.Double) {
                        register = "$f0";
                        mipsCode.add(new Code("l.d", register, variableDscp.addr + "($t0)"));
                        mipsCode.add(new Code("s.d", register, base + "($sp)"));


                    } else {
                        register = "$t3";
                        mipsCode.add(new Code("lw", register, variableDscp.addr + "($t0)"));
                        mipsCode.add(new Code("sw", register, base + "($sp)"));
                    }
                    base += variableDscp.type.size;
                    size += variableDscp.type.size;
                }

            } else if (dscp.dscpType == DscpType.array) {
                ArrayDscp arrayDscp = (ArrayDscp) dscp;
                mipsCode.add(new Code("lw", "$t3", arrayDscp.addr + "($t0)"));
                mipsCode.add(new Code("sw", "$t3", base + "($sp)"));
                base += 4;
                size += 4;
            }
        }
        return size;
    }

    public static int loadNotTempInSP(SymboleTable symboleTable, int base) {
        int size = 0;
        for (Map.Entry<Token, Dscp> me : symboleTable.entrySet()) {
            Dscp dscp = me.getValue();
            if (dscp.dscpType == DscpType.variable) {
                VariableDscp variableDscp = (VariableDscp) dscp;
                if (!variableDscp.isTemp) {
                    String register;
                    if (variableDscp.type.type == Type.Double) {
                        register = "$f0";
                        mipsCode.add(new Code("l.d", register, base + "($sp)"));

                        mipsCode.add(new Code("s.d", register, variableDscp.addr + "($t0)"));


                    } else {
                        register = "$t3";
                        mipsCode.add(new Code("lw", register, base + "($sp)"));
                        mipsCode.add(new Code("sw", register, variableDscp.addr + "($t0)"));
                    }
                    base += variableDscp.type.size;
                    size += variableDscp.type.size;
                }

            } else if (dscp.dscpType == DscpType.array) {
                ArrayDscp arrayDscp = (ArrayDscp) dscp;
                mipsCode.add(new Code("lw", "$t3", base + "($sp)"));
                mipsCode.add(new Code("sw", "$t3", arrayDscp.addr + "($t0)"));
                base += 4;
                size += 4;
            }
        }
        return size;
    }
}
