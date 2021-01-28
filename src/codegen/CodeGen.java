package codegen;

import codegen.discriptors.*;

import scanner.Token;
import scanner.TokenType;

import java.util.ArrayList;

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
        Dscp dscp;
        if (chum.getValue() == "]") {
            Token type = semanticStack.pop();
            dscp = (ArrayDscp) new ArrayDscp(type);
        } else {
            dscp = (VariableDscp) new VariableDscp(typeSetter(chum.getValue()), -1, false, false);
        }
        symboleTables.get(symboleTables.size() - 1).add(varName, dscp);
    }

    public static void CADSCP() {
        semanticStack.push(new Token("newArray"));
    }

    public static void cast(Dscp dscp, String type) {
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
                String s1 = d1.isImm ? "($t2)" : (d1.isTemp ? "($t1)" : "($t0)");
                String s2 = d2.isImm ? "($t2)" : (d2.isTemp ? "($t1)" : "($t0)");

                mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                if (d1.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    pc++;
                }
                mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
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
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
        }
        VarType type = getType(d1, d2, "Arith");
        VariableDscp d = new VariableDscp(type, temp, false, true);
        temp += type.size;
        Token t = null;

        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d2.value) - Integer.parseInt(d1.value)), TokenType.integer);
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("lw", "$t3", d2.addr + "($t0)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", Integer.toString(-1 * Integer.parseInt(d1.value))));
                    pc += 2;
                } else if (d2.isImm) { //2 - a
                    mipsCode.add(new Code("lw", "$t3", d1.addr + "($t0)"));
                    mipsCode.add(new Code("addi", "$t3", "$t3", Integer.toString(-1 * Integer.parseInt(d2.value)))); // a - 2
                    mipsCode.add(new Code("neg", "$t3", "$t3"));
                    pc += 3;
                } else {
                    mipsCode.add(new Code("lw", "$t3", d2.addr + "($t0)"));
                    mipsCode.add(new Code("lw", "$t4", d1.addr + "($t0)"));
                    mipsCode.add(new Code("sub", "$t3", "$t3", "$t4"));
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
                t = new Token(Double.toString(Double.parseDouble(d2.value) - Double.parseDouble(d1.value)), TokenType.real);
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
                String s1 = d1.isImm ? "($t2)" : (d1.isTemp ? "($t1)" : "($t0)");
                String s2 = d2.isImm ? "($t2)" : (d2.isTemp ? "($t1)" : "($t0)");

                mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                if (d1.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    pc++;
                }
                mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                if (d2.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    pc++;
                }
                mipsCode.add(new Code("sub.d", "$f0", "$f2", "$f0"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                pc += 4;
                t = new Token("$" + pc, TokenType.id);
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

        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d1.value) * Integer.parseInt(d2.value)), TokenType.integer);
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("li", "$t3", d1.value));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + "($t0)"));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t3", d2.value));
                    mipsCode.add(new Code("lw", "$t4", d1.addr + "($t0)"));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + "($t0)"));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + "($t0)"));
                }
                mipsCode.add(new Code("mult", "$t4", "$t3"));
                mipsCode.add(new Code("mflo", "$t3"));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                pc += 5;
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
                String s1 = d1.isImm ? "($t2)" : (d1.isTemp ? "($t1)" : "($t0)");
                String s2 = d2.isImm ? "($t2)" : (d2.isTemp ? "($t1)" : "($t0)");
                mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                if (d1.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    pc++;
                }
                mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                if (d2.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    pc++;
                }
                mipsCode.add(new Code("mul.d", "$f0", "$f0", "$f2"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                pc += 4;
                t = new Token("$" + pc, TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
        }

        semanticStack.push(t);
    }

    public static void DIVِ() {

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

        //start generating code for int result
        if (type.type == Type.Integer) {
            if (d1.isImm && d2.isImm) {
                t = new Token(Integer.toString(Integer.parseInt(d1.value) * Integer.parseInt(d2.value)), TokenType.integer);
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("li", "$t3", d1.value));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + "($t0)"));
                } else if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t3", d2.value));
                    mipsCode.add(new Code("lw", "$t4", d1.addr + "($t0)"));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + "($t0)"));
                    mipsCode.add(new Code("lw", "$t4", d2.addr + "($t0)"));
                }
                mipsCode.add(new Code("div", "$t4", "$t3"));
                mipsCode.add(new Code("mflo", "$t3"));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                pc += 5;
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
                String s1 = d1.isImm ? "($t2)" : (d1.isTemp ? "($t1)" : "($t0)");
                String s2 = d2.isImm ? "($t2)" : (d2.isTemp ? "($t1)" : "($t0)");

                mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                if (d1.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f0", "$f0"));
                    pc++;
                }
                mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                if (d2.type.type == Type.Integer) {
                    mipsCode.add(new Code("cvt.d.w", "$f2", "$f2"));
                    pc++;
                }
                mipsCode.add(new Code("div.d", "$f0", "$f2", "$f0"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                pc += 4;
                t = new Token("$" + pc, TokenType.id);
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
        VariableDscp d = new VariableDscp(new VarType(Type.String), stringAddr, false, true);
        Token t = new Token("$" + pc, TokenType.str_char);
        stringAddr += d.type.size;
        mipsCode.add(new Code("li", "$v0", "8"));
        mipsCode.add(new Code("la", "$a0", d.addr + "($t1)"));
        mipsCode.add(new Code("li", "$a1", "64"));
        mipsCode.add(new Code("syscall"));
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

    public static void SAVE() {
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void JZ() {

        Token token = semanticStack.pop();
        if(token.getType()==TokenType.undefined)
        {
            //error
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmtoBool(token, "$t3");
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

    public static void DJC() {
        Token token = semanticStack.pop();
        if(token.getType()==TokenType.undefined)
        {
            //error
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmtoBool(token, "$t3");
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
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
            }
        }
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmtoBool(token, "$t3");
            ;
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
                mipsCode.add(new Code("bnez", "$t3"));

            } else if (varBe.type.type == Type.Double) {

                mipsCode.add(new Code("Ld", "$f2", addr + src));
                SymboleTable.find(new Token("0", TokenType.real));
                mipsCode.add(new Code("Ld", "$f0", doubleAddr + "($t3)"));
                mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                mipsCode.add(new Code("bc1f"));
            } else {

                //error? :/
            }

        }
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));

        token = semanticStack.pop();
        if(token.getType()==TokenType.undefined)
        {
            //error
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmtoBool(token, "$t3");
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

    public static void FUNCTION() {
        Token name = token;
        Token returnType = semanticStack.pop();
        if (symboleTables.get(0).get(name) != null) {
            //dublicate name function error
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
    }

    public static void ADDPARAM() {
        int size = 0;
        Token inputName = semanticStack.pop();
        Token type = semanticStack.pop();
        if (symboleTables.get(symboleTables.size() - 1).get(inputName) != null) {
            //dublicate name input error
        } else {
            if (type.getValue().equals("]")) {
                type = semanticStack.pop();
                ArrayDscp arrayDscp = new ArrayDscp(type);
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

    public static void CFUNCTION() {
        ADDPARAM();
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        int spBase = funcDscp.size;
        spBase-=4;
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
                        mipsCode.add(new Code("lw", "$t3", spBase + "($sp)"));
                        mipsCode.add(new Code("sw", "$t3", dscp.addr + "($t0)"));
                        spBase -= 4;
                    }
            }
        }
        mipsCode.add(new Code("addi","($sp)","($sp)",String.valueOf(size)));
    }

    public static void ENDFUNCTION() {
        Token token = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp dscp = (FuncDscp) symboleTables.get(0).get(token);
        if (!dscp.returnType.equals("void") && !dscp.hasReturn) {
            // error  it need return
        }
        if(dscp.returnType.equals("void") &&!dscp.hasReturn){
            mipsCode.add(new Code("jr", "$ra"));
        }
        symboleTables.remove(symboleTables.size() - 1);
    }

    public static void RETURNP() {
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        Type type = null;

        if (funcDscp.returnType.equals("void")) {
            //error ->void
        }
        Token chum = semanticStack.pop();
        Dscp dscp = SymboleTable.find(chum);
        switch (dscp.dscpType) {
            case funcion:
            case record:
            case array:
                //error
                break;
            case variable:
                type = typeSetter(funcDscp.returnType).type;
                switch (((VariableDscp) dscp).type.type) {
                    case Integer:
                    case Double:
                    case Boolean:
                        if (!(type == Type.Integer || type == Type.Double || type == Type.Boolean)) {
                            //error
                        }
                        break;
                    case String:
                        if (!(type == Type.String || type == Type.Boolean)) {
                            //error
                        }
                        break;
                    case Record:
                        if (((VariableDscp) dscp).refType != funcDscp.returnType) {
                            //error
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
                mipsCode.add(new Code("lw", "$t0", variableDscp.addr + src));
                mipsCode.add(new Code("sw", "$t0", 0 + "($sp)"));
            }
        }
        mipsCode.add(new Code("jr", "$ra"));
    }

    public static void RETURNNP() {
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        if (!funcDscp.returnType.equals("void")) {
            //error
        } else {
            mipsCode.add(new Code("jr", "$ra"));
        }

    }

    public static void STARTFUNC() {
        Token funcName = semanticStack.pop();
        Dscp funcDscp = symboleTables.get(0).get(funcName);
        if (funcDscp == null || funcDscp.dscpType != DscpType.funcion) {
            //error
        }
        semanticStack.push(funcName);
        semanticStack.push(new Token("function"));
    }

    public static void CALFUNCNP() {
        Token funcName = semanticStack.pop();
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        VariableDscp variableDscp = null;
        if (funcDscp.inputNames.size() != 0) {
            //error
        }
        mipsCode.add(new Code("sw", "ra", "0($sp)"));
        mipsCode.add(new Code("jal", funcName.getValue() + ":"));
        semanticStack.pop();
        if (funcDscp.returnType == "void") {
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
                    mipsCode.add(new Code("l.d", "$f0", 4 + "($sp)"));
                    mipsCode.add(new Code("s.d", "$f0", temp + "($t1)"));
                    temp += 4;
                }
                Token t = new Token("$" + pc, TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, variableDscp);
                semanticStack.push(t);
            } else {
                //don't know
            }
        }


    }

    public static void CALFUNCP() {
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
        }
        int Sbase = funcDscp.size;
        mipsCode.add(new Code("addi","$sp","$sp", "-"+Sbase));
        Sbase-=4;
        mipsCode.add(new Code("sw","$ra",Sbase+"($sp)"));
        for (int i = tokens.size()-1; i >=0; i--) {
            Dscp funIn = SymboleTable.find(funcDscp.inputNames.get( tokens.size()-i-1));
            Dscp in = SymboleTable.find(tokens.get(i));
            if (funIn.dscpType != in.dscpType) {
                //error
            }
            if (in.dscpType == DscpType.funcion || in.dscpType == DscpType.record) {
                //error
            } else if (in.dscpType == DscpType.array) {
                if (((ArrayDscp) funIn).type.type != ((ArrayDscp) in).type.type) {
                    //error
                } else {
                    mipsCode.add(new Code("la", "$t3", in.addr + "($t1)"));
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
                        castImmtoBool(tokens.get(i), "$t3");
                    } else if (var.type.type == Type.Double || var.type.type == Type.Integer) {
                        castVariableToBool(var, base, "$t3", src);
                    } else if (var.type.type == Type.Boolean) {
                        mipsCode.add(new Code("lw", "$t3", var.addr + base));
                    } else {
                        //error
                        //string and record
                    }
                    Sbase -= 4;
                    mipsCode.add(new Code("sw", "$t3", Sbase + "($sp)"));
                } else if (funVar.type.type == Type.Integer) {
                    if (var.isImm) {
                        mipsCode.add(new Code("li", "$t3", String.valueOf(Integer.parseInt(tokens.get(i).getValue()))));
                    } else if (var.type.type == Type.Integer) {
                        mipsCode.add(new Code("lw", "$t3", var.addr + base));
                    } else if (var.type.type == Type.Double) {
                        castDoubleToInt(var, base, "$t3");
                    } else {
                        //error
                        //string and record
                    }
                    Sbase-=4;
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
                        //string and record
                    }
                }
                Sbase -= 8;
                mipsCode.add(new Code("sw", "$f0", Sbase + "($sp)"));
            }
        }
        mipsCode.add(new Code("sw", "ra", "0($sp)"));
        mipsCode.add(new Code("jal", funcName.getValue() + ":"));
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
                    ra =8;
                } else {
                    mipsCode.add(new Code("l.d", "$f0", 0 + "($sp)"));
                    mipsCode.add(new Code("s.d", "$f0", temp + "($t1)"));
                    temp += 4;
                    ra =4;
                }
                Token t = new Token("$" + pc, TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, variableDscp);
                semanticStack.push(t);
            } else {
                //don't know
            }
        }
        mipsCode.add(new Code("lw","$ra",ra+"($sp)"));
        mipsCode.add(new Code("addi","$sp","$sp", String.valueOf(ra+4)));
    }
}
