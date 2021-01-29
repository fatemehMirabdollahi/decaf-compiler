package codegen;

import codegen.discriptors.*;
import scanner.DecafScanner;
import scanner.Token;
import scanner.TokenType;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.concurrent.atomic.DoubleAccumulator;

import static Compiler.Compiler.*;
import static codegen.SymboleTable.*;
import static codegen.discriptors.Dscp.typeSetter;
import static parser.Parser.*;

public class CodeGen {

    public static void PUSH() {
        System.out.println(token);
        if (token.getType() == TokenType.id || token.getType() == TokenType.str_char || token.getType() == TokenType.real || token.getType() == TokenType.integer) {
            token.setValue(DecafScanner.value);
        }
        if (token.getValue().equals("colon"))
            token.setValue(",");
        semanticStack.push(token);
    }

    public static void MDSCP() throws Exception {
        Token varName = semanticStack.pop();
        Token chum = semanticStack.pop();
        if (symboleTables.get(symboleTables.size() - 1).get(varName) != null) {
            //error
            throw new Exception();
        }
        Dscp dscp;
        if (chum.getValue().equals("]")) {
            Token type = semanticStack.pop();
            dscp = (ArrayDscp) new ArrayDscp(type.getValue());
        } else {
            dscp = (VariableDscp) new VariableDscp(typeSetter(chum.getValue()), address, false, false);
            address += typeSetter(chum.getValue()).size;
        }
        symboleTables.get(symboleTables.size() - 1).add(varName, dscp);
    }

    public static void CADSCP() throws Exception {

        Token type = semanticStack.pop();
        Token size = semanticStack.pop();
        Dscp dSize = SymboleTable.find(size);


        if (dSize.dscpType != DscpType.variable || ((VariableDscp) dSize).type.type != Type.Integer) {
            //error
            throw new Exception();
        }

        ArrayDscp arrayDscp = new ArrayDscp(type.getValue());
        arrayDscp.addr = address;
        if (((VariableDscp) dSize).isImm)
            arrayDscp.size = ((VariableDscp) dSize).value;
        address += arrayDscp.type.size * Integer.parseInt(arrayDscp.size);
        Token t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, arrayDscp);
        semanticStack.push(t);

    }

    public static void ADD() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            throw new Exception();
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
                    mipsCode.add(new Code("addi", "$t3", "$t3", d1.value));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("addi", "$t3", "$t3", d2.value));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
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
                if (d1.type.type == Type.Integer || d1.type.type == Type.Boolean) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li.d", "$f0", d1.value + ".0"));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                        mipsCode.add(new Code("mtc1", "$t3", "$f0"));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }

                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                        mipsCode.add(new Code("mtc1", "$t3", "$f2"));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }

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

    public static void SUB() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            throw new Exception();
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
                    mipsCode.add(new Code("addi", "$t3", "$t3", Integer.toString(-1 * Integer.parseInt(d1.value))));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("addi", "$t3", "$t3", Integer.toString(-1 * Integer.parseInt(d1.value))));
                    mipsCode.add(new Code("neg", "$t3", "$t3"));
                } else {
                    mipsCode.add(new Code("lw", "$t4", d1.addr + s1));
                    mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
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
                if (d1.type.type == Type.Integer || d1.type.type == Type.Boolean) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li", "$f0", d1.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                        mipsCode.add(new Code("mtc1", "$t3", "$f0"));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }
                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                        mipsCode.add(new Code("mtc1", "$t3", "$f2"));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }
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

    public static void MUL() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            throw new Exception();
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
                } else if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t4", d2.value));
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
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
                if (d1.type.type == Type.Integer || d1.type.type == Type.Boolean) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li", "$f0", d1.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                        mipsCode.add(new Code("mtc1", "$t3", "$f0"));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }
                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                        mipsCode.add(new Code("mtc1", "$t3", "$f2"));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }
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

    public static void DIV() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            throw new Exception();
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
                } else if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t4", d2.value));
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
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
                if (d1.type.type == Type.Integer || d1.type.type == Type.Boolean) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li", "$f0", d1.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                        mipsCode.add(new Code("mtc1", "$t3", "$f0"));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }
                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                        mipsCode.add(new Code("mtc1", "$t3", "$f2"));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));

                    }
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

    public static void LE() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            throw new Exception();
        }
        VarType type = getType(d1, d2, "Compare");
        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
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
                    mipsCode.add(new Code("addi", "$t3", "$t3", d1.value));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("addi", "$t3", "$t3", d2.value));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
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
                if (d1.type.type == Type.Integer || d1.type.type == Type.Boolean) {

                    if (d1.isImm) {
                        mipsCode.add(new Code("li.d", "$f0", d1.value + ".0"));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                        mipsCode.add(new Code("mtc1", "$t3", "$f0"));
                        mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    }

                } else {
                    if (d1.isImm)
                        mipsCode.add(new Code("li.d", "$f0", d1.value));
                    else
                        mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                //second operand
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {

                    if (d2.isImm) {
                        mipsCode.add(new Code("li", "$f2", d2.value));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                        mipsCode.add(new Code("mtc1", "$t3", "$f2"));
                        mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    }

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

    public static void READINTEGER() throws Exception {
        VariableDscp d = new VariableDscp(new VarType(Type.Integer), temp, false, true);
        Token t = new Token("$" + mipsCode.size(), TokenType.id);
        temp += d.type.size;
        mipsCode.add(new Code("li", "$v0", "5"));
        mipsCode.add(new Code("la", "$a0", d.addr + "($t1)"));
        mipsCode.add(new Code("syscall"));
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void READLINE() throws Exception {

        VariableDscp d = new VariableDscp(new VarType(Type.String), temp, false, true);
        Token t = new Token("$" + mipsCode.size(), TokenType.str_char);
        int straddr = address;
        mipsCode.add(new Code("li", "$t3", Integer.toString(straddr)));
        address += d.type.size;
        mipsCode.add(new Code("sw", "$t3", temp + "($t1)"));
        mipsCode.add(new Code("li", "$v0", "8"));
        mipsCode.add(new Code("la", "$a0", straddr + "($t0)"));
        mipsCode.add(new Code("li", "$a1", "64"));
        mipsCode.add(new Code("syscall"));
        temp += 4;
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void PRINT() throws Exception {
        Token src = semanticStack.pop();
        VariableDscp d = (VariableDscp) SymboleTable.find(src);
        String funcNum = "";
        String base;
        if (d.addr == -1) {
            //error
            throw new Exception();
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
                mipsCode.add(new Code("l.d", "$f12", "0"+base));
                break;
            case String:
                funcNum = "4";
                mipsCode.add(new Code("lw", "$a0", d.addr + (d.isTemp ? "($t9)" : "($t0)")));

        }

        mipsCode.add(new Code("li", "$v0", funcNum));
        mipsCode.add(new Code("syscall"));


    }

    public static void ASSIGN() throws Exception {
        Token right = semanticStack.pop();
        Token left = semanticStack.pop();
        Dscp dRight = find(right);
        Dscp dLeft = find(left);
        String base;
        if (dRight.addr == -1) {
            //error
            throw new Exception();
        }
        if (dLeft.dscpType == DscpType.variable && dRight.dscpType == DscpType.variable) {

            VariableDscp dR = (VariableDscp) dRight;
            VariableDscp dL = (VariableDscp) dLeft;

            if (dL.isImm || dL.isTemp) { //?
                //error
                throw new Exception();
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
                        castDoubleToInt(dR,base,"$t3");
                        mipsCode.add(new Code("sw", "$t3", dL.addr + "($t0)"));
                        break;

                    default:
                        //error
                        throw new Exception();
                }
            } else if (dL.type.type == Type.Double) {

                if (dR.type.type == Type.Double) {
                    base = dR.isTemp ? "($t1)" : "($t0)";
                    if (dR.isImm) {
                        mipsCode.add(new Code("li.d", "$f0", dR.value));
                    } else {
                        mipsCode.add(new Code("l.d", "$f0", dR.addr + base));
                    }
                    mipsCode.add(new Code("s.d", "$f0", dL.addr + "($t0)"));

                } else if (dR.type.type == Type.Integer) {
                    base = dR.isTemp ? "($t1)" : "($t0)";
                    castIntToDouble(dR, base, "$f0");
                    mipsCode.add(new Code("s.d", "$f0", dL.addr + "($t0)"));

                } else {
                    //error
                    throw new Exception();
                }
            } else if (dL.type.type == Type.Boolean) {
                base = dR.isTemp ? "($t1)" : "($t0)";
                if (dR.type.type == Type.Boolean) {

                    mipsCode.add(new Code("lw", "$t3", dR.addr + base));
                    mipsCode.add(new Code("sw", "$t3", dL.addr + "($t0)"));

                } else if (dR.type.type == Type.Integer) {
                    if (dR.isImm) {
                        castImmToBool(left, "$t3");
                    } else {
                        castVariabelToBool(dR, base, "$t3", "$t4");
                    }
                    mipsCode.add(new Code("sw", "$t3", dL.addr + "($t0)"));

                } else if (dR.type.type == Type.Double) {
                    if (dR.isImm) {
                        castImmToBool(left, "$f0");
                    } else {
                        castVariabelToBool(dR, base, "$f0", "$f2");
                    }
                    mipsCode.add(new Code("sw", "$f0", dL.addr + "($t0)"));

                } else {
                    //error
                    throw new Exception();
                }
            } else if (dL.type.type == Type.Record) {

                if (dR.type.type == Type.Record && typeSetter(dR.refType) == typeSetter(dL.refType)) {
                    dL.addr = dR.addr;
                } else {
                    //error
                    throw new Exception();
                }
            } else if (dL.type.type == Type.String) {
                if (dR.type.type == Type.String) {
                    dL.addr = dR.addr;
                } else {
                    //error
                    throw new Exception();
                }
            } else {
                //error
                throw new Exception();
            }

        } else if (dLeft.dscpType == DscpType.array && dRight.dscpType == DscpType.array &&
                ((ArrayDscp) dLeft).type.equals(((ArrayDscp) dRight).type)) {

            ArrayDscp dR = (ArrayDscp) dRight;
            ArrayDscp dL = (ArrayDscp) dLeft;
            dL.addr = dR.addr;

        } else {
            //error
            throw new Exception();
        }


        //array = array check type ِ
        //int = int //int = double //int = boolean
        //double = double //double = int
        //boolean = boolean //boolean = int //boolean = double
        //record = record
        //string = string
        if (temp > maxTemp)
            maxTemp = temp;
        temp = 0;

    }

    public static void SAVE() {
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void JZ() throws Exception {

        Token token = semanticStack.pop();
        if (token.getType() == TokenType.undefined) {
            //error
            throw new Exception();
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmToBool(token, "$t3");
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            if (varBe.addr == -2) {
                //error
                throw new Exception();
            }
            String src;

            if (varBe.isTemp) {
                src = "($t1)";
                maxTemp = temp > maxTemp ? temp : maxTemp;
                temp = 0;
            } else {
                src = "($t0)";
            }
            if (varBe.type.type == Type.Integer || varBe.type.type == Type.Boolean) {

                mipsCode.add(new Code("l.d", "$t3", varBe.addr + src));
                mipsCode.add(new Code("beqz", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("l.d", "$f2", varBe.addr + src));
                mipsCode.add(new Code("ld.i", "$f0", "0.0"));
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                mipsCode.add(new Code("bc1t"));
            } else {

                //error? :/
                throw new Exception();
            }

        }
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));

    }

    public static void CWHILE() {
        Token jzPC = semanticStack.pop();
        while (jzPC.getType() == TokenType.undefined) {
            jzPC = semanticStack.pop();
        }
        while (jzPC.getValue() == "break") {
            Token breakPC = semanticStack.pop();
            mipsCode.get(Integer.parseInt(breakPC.getValue())).dest = "L" + labelNum + ":";
            jzPC = semanticStack.pop();
            while (jzPC.getType() == TokenType.undefined) {
                jzPC = semanticStack.pop();
            }
        }
        if (mipsCode.get(Integer.parseInt(jzPC.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).src1 = "L" + labelNum + ":";
        }
        Token whilePC = semanticStack.pop();
        mipsCode.add(new Code("b", mipsCode.get(Integer.parseInt(whilePC.getValue())).dest));
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
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
    }

    public static void JP() {
        mipsCode.remove(mipsCode.size() - 1);
        mipsCode.add(new Code("b"));
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));
        labelNum--;
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void CJP() {
        Token jpPC = semanticStack.pop();
        mipsCode.get(Integer.parseInt(jpPC.getValue())).dest = "L" + labelNum + ":";
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void BREAK() {
        mipsCode.add(new Code("b"));
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));
        semanticStack.push(new Token("break"));
    }

    public static void DJC() throws Exception {
        Token token = semanticStack.pop();
        if (token.getType() == TokenType.undefined) {
            //error
            throw new Exception();
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmToBool(token, "$t3");
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            if (varBe.addr == -1) {
                //error
                throw new Exception();
            }
            String src;
            int addr;
            if (varBe.isTemp) {
                src = "($t1)";
                maxTemp = temp > maxTemp ? temp : maxTemp;
                temp = 0;
            } else {
                src = "($t0)";
            }
            if (varBe.type.type == Type.Integer || varBe.type.type == Type.Boolean) {

                mipsCode.add(new Code("lw", "$t3", varBe.addr + src));
                mipsCode.add(new Code("beqz", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("l.d", "$f2", varBe.addr + src));
                mipsCode.add(new Code("l.d", "$f0", "0.0"));
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                mipsCode.add(new Code("bc1t"));
            } else {
                //error? :/
                throw new Exception();
            }
        }
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmToBool(token, "$t3");
            ;
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            if (varBe.addr == -1) {
                //error
                throw new Exception();
            }
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

                mipsCode.add(new Code("lw", "$t3", addr + src));
                mipsCode.add(new Code("bnez", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("ld", "$f2", addr + src));
                mipsCode.add(new Code("ld.i", "$f0", "0.0"));
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                mipsCode.add(new Code("bc1f"));
            } else {

                //error? :/
                throw new Exception();
            }

        }
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));

        token = semanticStack.pop();
        if (token.getType() == TokenType.undefined) {
            //error
            throw new Exception();
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmToBool(token, "$t3");
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            String src;

            if (varBe.isTemp) {
                src = "($t1)";
                maxTemp = temp > maxTemp ? temp : maxTemp;
                temp = 0;
            } else {
                src = "($t0)";
            }
            if (varBe.type.type == Type.Integer || varBe.type.type == Type.Boolean) {

                mipsCode.add(new Code("l.d", "$t3", varBe.addr + src));
                mipsCode.add(new Code("beqz", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("l.d", "$f2", varBe.addr + src));
                mipsCode.add(new Code("ld.i", "$f0", "0.0"));
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                mipsCode.add(new Code("bc1t"));
            } else {

                //error? :/
                throw new Exception();
            }

        }
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));

    }

    public static void CJNZ() {
        Token sstep = semanticStack.pop();
        Token jnz = semanticStack.pop();
        Token jz = semanticStack.pop();
        Token sbe = semanticStack.pop();
        mipsCode.add(new Code("b", mipsCode.get(Integer.parseInt(sbe.getValue())).dest));
        if (mipsCode.get(Integer.parseInt(jnz.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jnz.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jnz.getValue())).src1 = "L" + labelNum + ":";
        }
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        semanticStack.push(jz);
        semanticStack.push(sstep);
    }

    public static void CJZFOR() {
        Token sstep = semanticStack.pop();
        while (sstep.getType() == TokenType.undefined) {
            sstep = semanticStack.pop();
        }
        while (sstep.getValue() == "break") {
            Token breakPC = semanticStack.pop();
            mipsCode.get(Integer.parseInt(breakPC.getValue())).dest = "L" + labelNum + ":";
            sstep = semanticStack.pop();
            while (sstep.getType() == TokenType.undefined) {
                sstep = semanticStack.pop();
            }
        }
        mipsCode.add(new Code("b", mipsCode.get(Integer.parseInt(sstep.getValue())).dest));
        Token jz = semanticStack.pop();
        if (mipsCode.get(Integer.parseInt(jz.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jz.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jz.getValue())).src1 = "L" + labelNum + ":";
        }
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void FUNCTION() throws Exception {
        System.out.println(token.getValue());
        if(!token.getValue().equals("main"))
        token.setValue(DecafScanner.value);

        Token name = token;
        Token returnType = semanticStack.pop();
        if (symboleTables.get(0).get(name) != null) {
            //dublicate name function error
            throw new Exception();
        } else {
            FuncDscp dscp = new FuncDscp(returnType.getValue());
            if (!returnType.getValue().equals("void")) {
                Type type = typeSetter(returnType.getValue()).type;
            }
            symboleTables.get(0).add(name, dscp);
            dscp.symboleTable.name = name;
            symboleTables.add(dscp.symboleTable);
        }
        mipsCode.add(new Code("label", name.getValue() + ":"));
        if(name.getValue().equals("main")){
            mipsCode.add(new Code("la", "$t0","address"));
            mipsCode.add(new Code("la", "$t1","temp"));
        }
    }

    public static void ADDPARAM() throws Exception {
        int size = 0;
        Token inputName = semanticStack.pop();
        Token type = semanticStack.pop();
        if (symboleTables.get(symboleTables.size() - 1).get(inputName) != null) {
            //dublicate name input error
            throw new Exception();
        } else {
            if (type.getValue().equals("]")) {
                type = semanticStack.pop();
                ArrayDscp arrayDscp = new ArrayDscp(type.getValue());
                arrayDscp.addr = address;
                symboleTables.get(symboleTables.size() - 1).add(inputName, arrayDscp);
                address += 4;
                size = 4;
            } else if (type.getValue().equals("string")) {
                VarType v = new VarType(Type.String);
                v.size = 4;
                symboleTables.get(symboleTables.size() - 1).add(inputName, new VariableDscp(v, address, false, false));
                address += 4;
                size = 4;
            } else if (type.getValue().equals("int") || type.getValue().equals("double") || type.getValue().equals("bool")) {
                VariableDscp dscp = new VariableDscp(typeSetter(type.getValue()), address, false, false);
                symboleTables.get(symboleTables.size() - 1).add(inputName, dscp);
                address += typeSetter(type.getValue()).size;
                size = typeSetter(type.getValue()).size;

            } else if (type.getType() == TokenType.id) {
                RecordDscp d = (RecordDscp) symboleTables.get(0).get(type);
                if (d == null) {
                    //error
                    throw new Exception();
                }
                VarType v = new VarType(Type.Record);
                v.size = 4;
                VariableDscp variableDscp = new VariableDscp(v, address, false, false);
                variableDscp.refType = type.getValue();
                variableDscp.addr = address;
                symboleTables.get(symboleTables.size() - 1).add(inputName, variableDscp);
                address += 4;
                size = 4;
            }
        }
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        funcDscp.inputNames.add(inputName);
        funcDscp.size += size;
    }

    public static void CFUNCTION() throws Exception {
        ADDPARAM();
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        int spBase = funcDscp.size;
        spBase -= 4;
        int size = spBase;
        for (int i = 0; i < funcDscp.inputNames.size(); i++) {
            Dscp dscp = funcDscp.symboleTable.get(funcDscp.inputNames.get(i));
            switch (dscp.dscpType) {
                case array:
                    spBase = spBase - 4;
                    mipsCode.add(new Code("lw", "$t3", spBase + "($sp)"));
                    mipsCode.add(new Code("sw", "$t3", dscp.addr + "($t0)"));
                    break;
                case variable:
                    if (((VariableDscp) dscp).type.type == Type.Double) {
                        spBase -= 8;
                        mipsCode.add(new Code("l.d", "$f2", spBase + "($sp)"));
                        mipsCode.add(new Code("s.d", "$f2", dscp.addr + "($t0)"));
                    } else {
                        spBase -= 4;
                        mipsCode.add(new Code("lw", "$t3", spBase + "($sp)"));
                        mipsCode.add(new Code("sw", "$t3", dscp.addr + "($t0)"));

                    }
            }
        }
        mipsCode.add(new Code("addi", "$sp", "$sp", String.valueOf(size)));
    }

    public static void ENDFUNCTION() {
        Token token = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp dscp = (FuncDscp) symboleTables.get(0).get(token);
        if (!dscp.returnType.equals("void") && !dscp.hasReturn) {
            // error  it need return
        }
        if (dscp.returnType.equals("void") && !dscp.hasReturn) {
            mipsCode.add(new Code("jr", "$ra"));
        }
        symboleTables.remove(symboleTables.size() - 1);
    }

    public static void RETURNP() throws Exception {
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        Type type = null;

        if (funcDscp.returnType.equals("void")) {
            //error ->void
            throw new Exception();
        }
        Token chum = semanticStack.pop();
        Dscp dscp = SymboleTable.find(chum);
        switch (dscp.dscpType) {
            case funcion:
            case record:
            case array:
                //error
                throw new Exception();

            case variable:
                type = typeSetter(funcDscp.returnType).type;
                switch (((VariableDscp) dscp).type.type) {
                    case Integer:
                    case Double:
                    case Boolean:
                        if (!(type == Type.Integer || type == Type.Double || type == Type.Boolean)) {
                            //error
                            throw new Exception();
                        }
                        break;
                    case String:
                        if (!(type == Type.String || type == Type.Boolean)) {
                            //error
                            throw new Exception();
                        }
                        break;
                    case Record:
                        if (((VariableDscp) dscp).refType != funcDscp.returnType) {
                            //error
                            throw new Exception();
                        }
                        break;

                }
                break;
        }
        ((FuncDscp) symboleTables.get(0).get(funcName)).hasReturn = true;
        VariableDscp variableDscp = (VariableDscp) dscp;
        String src = variableDscp.isTemp ? "($t1)" : "($t0)";
        if (type == Type.Double) {
            mipsCode.add(new Code("addi", "$sp", "$sp", "-8"));
            if (((VariableDscp) dscp).isImm) {
                mipsCode.add(new Code("li.d", "$f0", variableDscp.value));
                mipsCode.add(new Code("s.d", "$f0", 0 + "($sp)"));

            } else {
                mipsCode.add(new Code("l.d", "$f0", variableDscp.addr + src));
                mipsCode.add(new Code("s.d", "$f0", 0 + "($sp)"));
            }
        } else {
            mipsCode.add(new Code("addi", "$sp", "$sp", "-4"));
            if (((VariableDscp) dscp).isImm) {
                mipsCode.add(new Code("li", "$t3", variableDscp.value));
                mipsCode.add(new Code("sw", "$t3", 0 + "($sp)"));

            } else {
                mipsCode.add(new Code("lw", "$t3", variableDscp.addr + src));
                mipsCode.add(new Code("sw", "$t3", 0 + "($sp)"));
            }
        }
        mipsCode.add(new Code("jr", "$ra"));
    }

    public static void RETURNNP() throws Exception {
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        if (!funcDscp.returnType.equals("void")) {
            //error
            throw new Exception();
        } else {
            mipsCode.add(new Code("jr", "$ra"));
        }

    }

    public static void STARTFUNC() throws Exception {
        Token funcName = semanticStack.pop();
        System.out.println(funcName.getValue());
        Dscp funcDscp = symboleTables.get(0).get(funcName);
        if (funcDscp == null || funcDscp.dscpType != DscpType.funcion) {
            System.out.println(funcDscp.dscpType);
            //error
            throw new Exception();
        }
        semanticStack.push(funcName);
        semanticStack.push(new Token("function"));
    }

    public static void CALLFUNCNP() throws Exception {
        semanticStack.pop();
        Token funcName = semanticStack.pop();
        System.out.println(funcName.getValue());
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        VariableDscp variableDscp = null;
        System.out.println(funcDscp.inputNames);
        if (funcDscp.inputNames.size() != 0) {
            //error
            throw new Exception();
        }
        mipsCode.add(new Code("sw", "$ra", "0($sp)"));
        mipsCode.add(new Code("jal", funcName.getValue()));

        if (funcDscp.returnType.equals("void")) {
            semanticStack.push(new Token("void", TokenType.undefined));
        } else {
            VarType type = typeSetter(funcDscp.returnType);
            if (type.type != Type.Record) {
                variableDscp = new VariableDscp(type, temp, false, true);
                if (type.type == Type.Double) {
                    mipsCode.add(new Code("l.d", "$f0", 8 + "($sp)"));
                    mipsCode.add(new Code("s.d", "$f0", temp + "($t1)"));
                    temp += 8;
                } else {
                    mipsCode.add(new Code("lw", "$t3", 4 + "($sp)"));
                    mipsCode.add(new Code("sw", "$t3", temp + "($t1)"));
                    temp += 4;
                }
                Token t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, variableDscp);
                semanticStack.push(t);
            } else {
                //don't know
            }
        }


    }

    public static void CALLFUNCP() throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        while (true) {
            Token token = semanticStack.pop();
            if (token.getValue().equals("function"))
                break;
            tokens.add(token);
        }
        Token funcName = semanticStack.pop();
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        if (tokens.size() != funcDscp.inputNames.size()) {
            //error
            throw new Exception();
        }
        int Sbase = funcDscp.size;
        mipsCode.add(new Code("addi", "$sp", "$sp", "-" + Sbase));
        Sbase -= 4;
        mipsCode.add(new Code("sw", "$ra", Sbase + "($sp)"));
        for (int i = tokens.size() - 1; i >= 0; i--) {
            System.out.println(funcDscp.inputNames.get(tokens.size() - i - 1).getValue());
            Dscp funIn = funcDscp.symboleTable.get(funcDscp.inputNames.get(tokens.size() - i - 1));
            Dscp in = SymboleTable.find(tokens.get(i));
            if (in.addr == -1) {
                //error
                throw new Exception();
            }
            if (funIn.dscpType != in.dscpType) {
                //error
                throw new Exception();
            }
            if (in.dscpType == DscpType.funcion || in.dscpType == DscpType.record) {
                //error
                throw new Exception();
            } else if (in.dscpType == DscpType.array) {
                if (((ArrayDscp) funIn).type.type != ((ArrayDscp) in).type.type) {
                    //error
                    throw new Exception();
                } else {
                    mipsCode.add(new Code("lw", "$t3", in.addr + "($t1)"));
                    Sbase -= 4;
                    mipsCode.add(new Code("sw", "$t3", Sbase + "($sp)"));
                }
            } else {
                VariableDscp funVar = (VariableDscp) funIn;
                VariableDscp var = (VariableDscp) in;
                String base = var.isTemp ? "($t1)" : "($t0)";
                String src = var.type.type == Type.Double ? "$f2" : "$t4";
                if (funVar.type.type == Type.Boolean) {
                    if (var.isImm) {
                        castImmToBool(tokens.get(i), "$t3");
                    } else if (var.type.type == Type.Double || var.type.type == Type.Integer) {
                        castVariabelToBool(var, base, "$t3", src);
                    } else if (var.type.type == Type.Boolean) {
                        mipsCode.add(new Code("lw", "$t3", var.addr + base));
                    } else {
                        //error
                        throw new Exception();
                        //string and record
                    }
                    Sbase -= 4;
                    mipsCode.add(new Code("sw", "$t3", Sbase + "($sp)"));
                } else if (funVar.type.type == Type.Integer) {
                    if (var.isImm) {
                        mipsCode.add(new Code("li", "$t3", String.valueOf((int) Double.parseDouble(tokens.get(i).getValue()))));
                    } else if (var.type.type == Type.Integer) {
                        mipsCode.add(new Code("lw", "$t3", var.addr + base));
                    } else if (var.type.type == Type.Double) {
                        castDoubleToInt(var, base, "$t3");
                    } else {
                        //error
                        throw new Exception();
                        //string and record
                    }
                    Sbase -= 4;
                    mipsCode.add(new Code("sw", "$t3", Sbase + "($sp)"));
                } else if (funVar.type.type == Type.Double) {
                    if (var.isImm) {
                        mipsCode.add(new Code("li.d", "$f0", String.valueOf(Double.parseDouble(tokens.get(i).getValue()))));
                    } else if (var.type.type == Type.Integer) {
                        castIntToDouble(var, base, "$f0");
                    } else if (var.type.type == Type.Double) {
                        mipsCode.add(new Code("l.d", "$f0", var.addr + base));
                    } else {
                        //error
                        throw new Exception();
                        //string and record
                    }
                    Sbase -= 8;
                    mipsCode.add(new Code("s.d", "$f0", Sbase + "($sp)"));
                }

            }
        }
        mipsCode.add(new Code("jal", funcName.getValue()));
        VariableDscp variableDscp = null;
        int ra = 0;
        if (funcDscp.returnType.equals("void")) {
            semanticStack.push(new Token("void", TokenType.undefined));
        } else {

            VarType type = typeSetter(funcDscp.returnType);
            if (type.type != Type.Record) {
                variableDscp = new VariableDscp(type, temp, false, true);
                if (type.type == Type.Double) {
                    mipsCode.add(new Code("l.d", "$f0", 0 + "($sp)"));
                    mipsCode.add(new Code("s.d", "$f0", temp + "($t1)"));
                    temp += 8;
                    ra = 8;
                } else {
                    mipsCode.add(new Code("lw", "$t3", 0 + "($sp)"));
                    mipsCode.add(new Code("sw", "$t3", temp + "($t1)"));
                    temp += 4;
                    ra = 4;
                }
                Token t = new Token("$" + mipsCode.size(), TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, variableDscp);
                semanticStack.push(t);
            } else {
                //don't know
            }
        }
        mipsCode.add(new Code("lw", "$ra", ra + "($sp)"));
        mipsCode.add(new Code("addi", "$sp", "$sp", String.valueOf(ra + 4)));

    }

}
