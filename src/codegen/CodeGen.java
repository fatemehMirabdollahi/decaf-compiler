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
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
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
                mipsCode.add(new Code("div.d", "$f0", "$f2", "$f0"));
                mipsCode.add(new Code("s.d", "$f0", d.addr + "($t1)"));
                pc += 4;
                t = new Token("$" + pc, TokenType.id);
                symboleTables.get(symboleTables.size() - 1).add(t, d);
            }
        }

        semanticStack.push(t);
    }

    public static void READINTEGER(){
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

    public static void READLINE(){
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
    public static void CJZFOR(){
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
