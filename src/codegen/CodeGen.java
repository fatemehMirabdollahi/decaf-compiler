package codegen;

import codegen.discriptors.Dscp;
import codegen.discriptors.Type;
import codegen.discriptors.VarType;
import codegen.discriptors.VariableDscp;
import scanner.Token;
import scanner.TokenType;

import static codegen.SymboleTable.*;
import static parser.Parser.*;

public class CodeGen {
    public static void PUSH() {
        semanticStack.push(token);
    }

    public static void cast(Dscp dscp, String type) {
    }

    public static void ADD() {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        VarType type = getType(d1, d2, "Arith");
        VariableDscp d = new VariableDscp(type, temp, false, true);
        temp += type.size;
        Token t = null;

        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d1.value) + Integer.parseInt(d2.value)), TokenType.integer);
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d2.addr + "($t0)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", d1.value));
                    pc += 2;
                } else if (d2.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + "($t0)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", d2.value));
                    pc += 2;
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + "($t0)"));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + "($t0)"));
                    mipsCode.add(new Code("add", "$t3", "$t3", "$t4"));
                    pc += 3;
                }
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                pc++;
                t = new Token("$" + pc, TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
            //start generating code for double result
        } else {
            if (d1.isImm && d2.isImm) {
                t = new Token(Double.toString(Double.parseDouble(d1.value) + Double.parseDouble(d2.value)), TokenType.real);
                find(t);
            } else {
                if (d1.isImm && d1.type.type == Type.Integer) {
                    src1.setType(TokenType.real);
                    d1 = (VariableDscp) SymboleTable.find(src1);
                }
                if (d2.isImm && d2.type.type == Type.Integer) {
                    src2.setType(TokenType.real);
                    d2 = (VariableDscp) SymboleTable.find(src2);
                }
                mipsCode.add(new Code("l.d", "$f0", d1.addr + "($t2)"));
                if (d1.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    pc++;
                }
                mipsCode.add(new Code("l.d", "$f2", d2.addr + "($t2)"));
                if (d2.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    pc++;
                }
                mipsCode.add(new Code("add.d", "$f0", "$f0", "$f2"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                pc += 4;
                t = new Token("$" + pc, TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
        }
        
        semanticStack.push(t);
    }

    public static void SUB() {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        VarType type = getType(dscp1, dscp2, "Arith");
        VariableDscp d = new VariableDscp(type, temp, false, true);
        temp += type.size;
        Token t = null;

        VariableDscp d1 = (VariableDscp) dscp1;
        VariableDscp d2 = (VariableDscp) dscp2;

        //start generating code for int int
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d2.value) - Integer.parseInt(d1.value)), TokenType.integer);
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("lw", "$t2", dscp2.addr + "($t0)"));
                    mipsCode.add(new Code("addi", "$t2", "$t2", Integer.toString(-1 * Integer.parseInt(d1.value))));
                    pc += 2;
                } else if (d2.isImm) {
                    mipsCode.add(new Code("lw", "$t2", dscp1.addr + "($t0)"));
                    mipsCode.add(new Code("addi", "$t2", "$t2", d2.value));
                    //javab dar manfi yek zarb she
                    pc += 3;
                } else {
                    mipsCode.add(new Code("lw", "$t2", dscp1.addr + "($t0)"));
                    mipsCode.add(new Code("lw", "$t3", dscp2.addr + "($t0)"));
                    mipsCode.add(new Code("sub", "$t3", "$t3", "$t2"));
                    pc += 3;
                }
                t = new Token("$" + pc, TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
//

        } else {
            if (d1.isImm && d2.isImm) {
                t = new Token(Double.toString(Double.parseDouble(d2.value) - Double.parseDouble(d1.value)), TokenType.integer);
            } else {
                if (d1.isImm) {

                }
            }
        }

        //
        semanticStack.push(t);
    }

    public static void MUL() {
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "Arith");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("mul", dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void DIV() {
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "Arith");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("div", dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void LT() {
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("lessThan", dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void LE() {
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("lessEqual", dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void GT() {
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("graterThan", dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void GE() {
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("graterEqual", dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void SAVE() {
        semanticStack.push(String.valueOf(pc));
    }

    public static void JZ() {
        String src = semanticStack.pop();

    }


    public static void EQUAL() {
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "equal");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("equal", dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }
}
