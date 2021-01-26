package codegen;

import codegen.discriptors.*;
import scanner.Token;
import scanner.TokenType;

import static codegen.SymboleTable.*;
import static codegen.discriptors.Dscp.typeSetter;
import static parser.Parser.*;

public class CodeGen {
    public static void PUSH() {
        semanticStack.push(token);
    }

    public static void MDSCP() {
        Token varName = semanticStack.pop();
        Token chum = semanticStack.pop();
        if (symboleTables.get(symboleTables.size() - 1).get(varName) != null) {
            //error
        }
        Dscp dscp;
        if (chum.getValue() == "]") {
            Token type = semanticStack.pop();
            dscp = (ArrayDscp) new ArrayDscp(type.getValue());
        } else {
            dscp = (VariableDscp) new VariableDscp(typeSetter(chum.getValue()), -1, false, false);
        }
        symboleTables.get(symboleTables.size() - 1).add(varName, dscp);
    }

    public static void CADSCP() {

        Token type = semanticStack.pop();
        Token size = semanticStack.pop();
        Dscp dSize = SymboleTable.find(size);


        if (dSize.dscpType != DscpType.variable || ((VariableDscp) dSize).type.type != Type.Integer) {
            //error
        }

        ArrayDscp arrayDscp = new ArrayDscp(type.getValue());
        arrayDscp.addr = address;
        if(((VariableDscp) dSize).isImm)
            arrayDscp.size = ((VariableDscp)dSize).value;
        address += arrayDscp.type.size * Integer.parseInt(arrayDscp.size);
        Token t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, arrayDscp);
        semanticStack.push(t);

    }

    public static void ADD() {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
        }
        VarType type = getType(d1, d2, "Arith");
        VariableDscp d = new VariableDscp(type, temp, false, true);
        temp += type.size;
        Token t = null;
        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d1.value) + Integer.parseInt(d2.value)), TokenType.integer);
            } else {

                if (d1.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", d1.value));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", d2.value));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t4", "0($t4)"));
                    mipsCode.add(new Code("add", "$t3", "$t3", "$t4"));
                }
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
            //start generating code for double result
        } else {
            if (d1.isImm && d2.isImm) {
                t = new Token(Double.toString(Double.parseDouble(d1.value) + Double.parseDouble(d2.value)), TokenType.real);
            } else {
                //first operand
                if (d1.type.type == Type.Integer) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li.d", "$f0", d1.value + ".0"));
                    } else {
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }

                } else if (d1.type.type == Type.Array) {

                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("l.d", "$f0", "0($t3)"));
                    if (typeSetter(d1.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));

                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }

                } else if (d2.type.type == Type.Array) {

                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                    mipsCode.add(new Code("l.d", "$f2", "0($t3)"));
                    if (typeSetter(d2.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));

                } else {
                    if (d2.isImm)
                        mipsCode.add(new Code("li.d", "$f2", d2.value));
                    else
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                }

                mipsCode.add(new Code("add.d", "$f0", "$f0", "$f2"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
        }
        semanticStack.push(t);
    }

    public static void SUB() {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
        }
        VarType type = getType(d1, d2, "Arith");
        VariableDscp d = new VariableDscp(type, temp, false, true);
        temp += type.size;
        Token t = null;
        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d2.value) - Integer.parseInt(d1.value)), TokenType.integer);
            } else {

                if (d1.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", Integer.toString(-1 * Integer.parseInt(d1.value))));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", Integer.toString(-1 * Integer.parseInt(d1.value))));
                    mipsCode.add(new Code("neg", "$t3", "$t3"));
                } else {
                    mipsCode.add(new Code("lw", "$t4", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t4", "0($t4)"));
                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("sub", "$t3", "$t3", "$t4"));
                }
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
            //start generating code for double result
        } else {
            if (d1.isImm && d2.isImm) {
                t = new Token(Double.toString(Double.parseDouble(d2.value) - Double.parseDouble(d1.value)), TokenType.real);
            } else {
                //first operand
                if (d1.type.type == Type.Integer) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li", "$f0", d1.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }

                } else if (d1.type.type == Type.Array) {

                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("l.d", "$f0", "0($t3)"));
                    if (typeSetter(d1.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));

                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }

                } else if (d2.type.type == Type.Array || d2.type.type == Type.Record) {

                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                    mipsCode.add(new Code("l.d", "$f2", "0($t3)"));
                    if (typeSetter(d2.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));

                } else {
                    if (d2.isImm)
                        mipsCode.add(new Code("li.d", "$f2", d2.value));
                    else
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                }

                mipsCode.add(new Code("sub.d", "$f0", "$f2", "$f0"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
        }
        semanticStack.push(t);
    }

    public static void MUL() {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
        }
        VarType type = getType(d1, d2, "Arith");
        VariableDscp d = new VariableDscp(type, temp, false, true);
        temp += type.size;
        Token t = null;
        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d2.value) * Integer.parseInt(d1.value)), TokenType.integer);
            } else {

                if (d1.isImm) {
                    mipsCode.add(new Code("li", "$t3", d1.value));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t4", "0($t4)"));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t4", d2.value));
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t4", "0($t4)"));
                }
                mipsCode.add(new Code("mult", "$t4", "$t3"));
                mipsCode.add(new Code("mflo", "$t3"));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
            //start generating code for double result
        } else {
            if (d1.isImm && d2.isImm) {
                t = new Token(Double.toString(Double.parseDouble(d2.value) * Double.parseDouble(d1.value)), TokenType.real);
            } else {
                //first operand
                if (d1.type.type == Type.Integer) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li", "$f0", d1.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }

                } else if (d1.type.type == Type.Array) {

                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("l.d", "$f0", "0($t3)"));
                    if (typeSetter(d1.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));

                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }

                } else if (d2.type.type == Type.Array) {

                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                    mipsCode.add(new Code("l.d", "$f2", "0($t3)"));
                    if (typeSetter(d2.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));

                } else {
                    if (d2.isImm)
                        mipsCode.add(new Code("li.d", "$f2", d2.value));
                    else
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s1));
                }

                mipsCode.add(new Code("mul.d", "$f0", "$f2", "$f0"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
        }
        semanticStack.push(t);
    }

    public static void DIV() {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
        }
        VarType type = getType(d1, d2, "Arith");
        VariableDscp d = new VariableDscp(type, temp, false, true);
        temp += type.size;
        Token t = null;
        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d2.value) / Integer.parseInt(d1.value)), TokenType.integer);
            } else {

                if (d1.isImm) {
                    mipsCode.add(new Code("li", "$t3", d1.value));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t4", "0($t4)"));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t4", d2.value));
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    if (d1.type.type == Type.Array || d1.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t3", "0($t3)"));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                    if (d2.type.type == Type.Array || d2.type.type == Type.Record)
                        mipsCode.add(new Code("lw", "$t4", "0($t4)"));
                }
                mipsCode.add(new Code("div", "$t4", "$t3"));
                mipsCode.add(new Code("mflo", "$t3"));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
            //start generating code for double result
        } else {
            if (d1.isImm && d2.isImm) {
                t = new Token(Double.toString(Double.parseDouble(d2.value) / Double.parseDouble(d1.value)), TokenType.real);
            } else {
                //first operand
                if (d1.type.type == Type.Integer) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li", "$f0", d1.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }

                } else if (d1.type.type == Type.Array) {

                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("l.d", "$f0", "0($t3)"));
                    if (typeSetter(d1.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));

                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));

                        if (d1.type.type == Type.Integer)
                            mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }

                } else if (d2.type.type == Type.Array || d2.type.type == Type.Record) {

                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                    mipsCode.add(new Code("l.d", "$f2", "($t3)"));
                    if (typeSetter(d2.refType).type == Type.Integer)
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));

                } else {
                    if (d2.isImm)
                        mipsCode.add(new Code("li.d", "$f2", d2.value));
                    else
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                }

                mipsCode.add(new Code("div.d", "$f0", "$f2", "$f0"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
        }
        semanticStack.push(t);
    }

    public static void READINTEGER() {
        pc += 3;
        VariableDscp d = new VariableDscp(new VarType(Type.Integer), temp, false, true);
        Token t = new Token("$" + pc, TokenType.id);
        temp += d.type.size;
        mipsCode.add(new Code("li", "$v0", "5"));
        mipsCode.add(new Code("la", "$a0", d.addr + "($t1)"));
        mipsCode.add(new Code("syscall"));
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void READLINE() {
        pc += 4;
        VariableDscp d = new VariableDscp(new VarType(Type.String), address, false, true);
        Token t = new Token("$" + pc, TokenType.str_char);
        mipsCode.add(new Code("li", "$t3", Integer.toString(stringAddr)));
        mipsCode.add(new Code("sw", "$t3", address + "($t0)"));
        mipsCode.add(new Code("li", "$v0", "8"));
        mipsCode.add(new Code("la", "$a0", stringAddr + "($t1)"));
        mipsCode.add(new Code("li", "$a1", "64"));
        mipsCode.add(new Code("syscall"));
        stringAddr += d.type.size;
        address += 4;
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void PRINT() {
        Token src = semanticStack.pop();
        VariableDscp d = (VariableDscp) SymboleTable.find(src);
        String funcNum = "";
        String base;
        if (d.addr == -1) {
            //error
        }
        switch (d.type.type) {
            case Integer:
            case Boolean:
                funcNum = "1";
                if (d.isImm) {
                    mipsCode.add(new Code("li", "$a0", d.value));
                } else {
                    mipsCode.add(new Code("lw", "$a0", d.addr + (d.isTemp ? "($t1)" : "($t0)")));
                }
                break;
            case Double:
                funcNum = "3";
                base = d.isImm ? "($t2)" : (d.isTemp ? "($t1)" : "($t0)");
                mipsCode.add(new Code("l.d", "$f12", base));

            case String:
                funcNum = "4";
                mipsCode.add(new Code("lw", "$a0", d.addr + (d.isTemp ? "($t9)" : "($t0)")));

        }

        mipsCode.add(new Code("li", "$v0", funcNum));
        mipsCode.add(new Code("syscall"));

        pc += 3;

    }

    public static void ASSIGN() {

        Token right = semanticStack.pop();
        Token left = semanticStack.pop();
        Dscp dRight = SymboleTable.find(right);
        Dscp dLeft = SymboleTable.find(left);
        String base;

        if (dLeft.dscpType == DscpType.variable && dRight.dscpType == DscpType.variable) {

            VariableDscp dR = (VariableDscp) dRight;
            VariableDscp dL = (VariableDscp) dLeft;

            if (dL.isImm || dL.isTemp) {
                //error
            }

            if (dL.type.type == Type.Integer) {

                switch (dR.type.type) {
                    case Integer:
                    case Boolean:
                        if (dR.isImm) {
                            mipsCode.add(new Code("li", "$t3", dR.value));

                        } else {
                            base = dR.isTemp ? "($t1)" : "($t0)";
                            mipsCode.add(new Code("lw", "$t3", dR.addr + base));
                        }
                        mipsCode.add(new Code("sw", "$t3", dL.addr + "($t0)"));
                        break;

                    case Double:
                        base = dR.isTemp ? "($t1)" : "($t0)";
                        if (dR.isImm)
                            mipsCode.add(new Code("li.d", "$f0", dR.value));
                        else
                            mipsCode.add(new Code("l.d", "$f0", dR.addr + base));
                        mipsCode.add(new Code("cvt.w.d", "$f0", "$f0"));
                        mipsCode.add(new Code("sw", "$f0", dL.addr + "($t0)")); //does it work?
                        break;


                    case Array:
                        mipsCode.add(new Code("lw", "$t3", dR.addr + "($t1)"));
                        mipsCode.add(new Code("lw", "$t4", "($t3)"));


                        break;
                    case Record:
                        //we don't know :|
                    default:
                        //error
                }
            }


        } else if (dLeft.dscpType == DscpType.array && dRight.dscpType == DscpType.array &&
                ((ArrayDscp) dLeft).type.equals(((ArrayDscp) dRight).type) &&
                ((ArrayDscp) dLeft).size.equals(((ArrayDscp) dRight).size)) {

            ArrayDscp dR = (ArrayDscp) dRight;
            ArrayDscp dL = (ArrayDscp) dLeft;
            dL.addr = dR.addr;

        } else {
            //error
        }


        //array = array check type ِ                                                   DONE
        //int = int //int = double //int = boolean
        //double = double //double = int
        //boolean = boolean //boolean = int //boolean = double
        //record = record
        //string = string
        temp = 0;

    }

    public static void SAVE() {
        semanticStack.push(new Token(String.valueOf(pc), TokenType.pc));
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;
    }

    public static void JZ() {
        Token token = semanticStack.pop();
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            int be = castBe(token);
            mipsCode.add(new Code("li", "$t3", String.valueOf(be)));
            pc++;
            mipsCode.add(new Code("beqz", "$t3"));
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            String src;
            int addr;
            if (varBe.isTemp) {
                src = "($t1)";
                addr = temp;
                maxTemp = temp > maxTemp ? temp : maxTemp;
                temp = 0;
            } else {
                src = "($t0)";
                addr = address;
            }
            if (varBe.type.type == Type.Integer || varBe.type.type == Type.Boolean) {

                mipsCode.add(new Code("LW", "$t3", addr + src));
                pc++;
                mipsCode.add(new Code("beqz", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("Ld", "$f2", addr + src));
                pc++;
                SymboleTable.find(new Token("0", TokenType.real));
                mipsCode.add(new Code("Ld", "$f0", doubleAddr + "($t3)"));
                pc++;
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                pc++;
                mipsCode.add(new Code("bc1t"));
            } else {

                //error? :/
            }

        }
        semanticStack.push(new Token(String.valueOf(pc), TokenType.pc));
        pc++;

    }

    public static void CWHILE() {
        Token jzPC = semanticStack.pop();
        while (jzPC.getValue() == "break") {
            Token breakPC = semanticStack.pop();
            mipsCode.get(Integer.parseInt(breakPC.getValue())).dest = "L" + labelNum + ":";
            jzPC = semanticStack.pop();
        }
        if (mipsCode.get(Integer.parseInt(jzPC.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).src1 = "L" + labelNum + ":";
        }
        Token whilePC = semanticStack.pop();
        mipsCode.add(new Code("b", mipsCode.get(Integer.parseInt(whilePC.getValue())).dest));
        pc++;
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;

    }

    public static void CJZ() {
        Token jzPC = semanticStack.pop();
        if (mipsCode.get(Integer.parseInt(jzPC.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).src1 = "L" + labelNum + ":";
        }
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;
    }

    public static void JP() {
        mipsCode.remove(mipsCode.size() - 1);
        pc--;
        mipsCode.add(new Code("b"));
        semanticStack.push(new Token(String.valueOf(pc), TokenType.pc));
        pc++;

        labelNum--;
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;
    }

    public static void CJP() {
        Token jpPC = semanticStack.pop();
        mipsCode.get(Integer.parseInt(jpPC.getValue())).dest = "L" + labelNum + ":";
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;
    }

    public static void BREAK() {
        mipsCode.add(new Code("b"));
        semanticStack.push(new Token(String.valueOf(pc), TokenType.pc));
        pc++;
        semanticStack.push(new Token("break"));
    }

    public static void DJC() {
        Token token = semanticStack.pop();
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            int be = castBe(token);
            mipsCode.add(new Code("li", "$t3", String.valueOf(be)));
            pc++;
            mipsCode.add(new Code("beqz", "$t3"));
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            String src;
            int addr;
            if (varBe.isTemp) {
                src = "($t1)";
                addr = temp;
                maxTemp = temp > maxTemp ? temp : maxTemp;
                temp = 0;
            } else {
                src = "($t0)";
                addr = address;
            }
            if (varBe.type.type == Type.Integer || varBe.type.type == Type.Boolean) {

                mipsCode.add(new Code("LW", "$t3", addr + src));
                pc++;
                mipsCode.add(new Code("beqz", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("Ld", "$f2", addr + src));
                pc++;
                SymboleTable.find(new Token("0", TokenType.real));
                mipsCode.add(new Code("Ld", "$f0", doubleAddr + "($t3)"));
                pc++;
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                pc++;
                mipsCode.add(new Code("bc1t"));
            } else {
                //error? :/
            }
        }
        semanticStack.push(new Token(String.valueOf(pc), TokenType.pc));
        pc++;
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            int be = castBe(token);
            mipsCode.add(new Code("li", "$t3", String.valueOf(be)));
            pc++;
            mipsCode.add(new Code("bgtz", "$t3"));
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            String src;
            int addr;
            if (varBe.isTemp) {
                src = "($t1)";
                addr = temp;
                maxTemp = temp > maxTemp ? temp : maxTemp;
                temp = 0;
            } else {
                src = "($t0)";
                addr = address;
            }
            if (varBe.type.type == Type.Integer || varBe.type.type == Type.Boolean) {

                mipsCode.add(new Code("LW", "$t3", addr + src));
                pc++;
                mipsCode.add(new Code("bgtz", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("Ld", "$f2", addr + src));
                pc++;
                SymboleTable.find(new Token("0", TokenType.real));
                mipsCode.add(new Code("Ld", "$f0", doubleAddr + "($t3)"));
                pc++;
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                pc++;
                mipsCode.add(new Code("bc1f"));
            } else {

                //error? :/
            }

        }
        semanticStack.push(new Token(String.valueOf(pc), TokenType.pc));
        pc++;

    }

    public static void CJNZ() {
        Token sstep = semanticStack.pop();
        Token jnz = semanticStack.pop();
        Token jz = semanticStack.pop();
        Token sbe = semanticStack.pop();
        mipsCode.add(new Code("b", mipsCode.get(Integer.parseInt(sbe.getValue())).dest));
        pc++;
        if (mipsCode.get(Integer.parseInt(jnz.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jnz.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jnz.getValue())).src1 = "L" + labelNum + ":";
        }
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;
        semanticStack.push(jz);
        semanticStack.push(sstep);
    }

    public static void CJZFOR() {
        Token sstep = semanticStack.pop();
        while (sstep.getValue() == "break") {
            Token breakPC = semanticStack.pop();
            mipsCode.get(Integer.parseInt(breakPC.getValue())).dest = "L" + labelNum + ":";
            sstep = semanticStack.pop();
        }
        mipsCode.add(new Code("b", mipsCode.get(Integer.parseInt(sstep.getValue())).dest));
        pc++;
        Token jz = semanticStack.pop();
        if (mipsCode.get(Integer.parseInt(jz.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jz.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jz.getValue())).src1 = "L" + labelNum + ":";
        }
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;
    }
}
