package codegen;

import codegen.discriptors.*;
import javafx.util.Pair;
import scanner.DecafScanner;
import scanner.Token;
import scanner.TokenType;

import java.awt.*;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.concurrent.atomic.DoubleAccumulator;

import static Compiler.Compiler.*;
import static codegen.SymboleTable.*;
import static codegen.discriptors.Dscp.typeSetter;
import static parser.Parser.*;

public class CodeGen {

    public static void ARRLENGTH() throws Exception {
        Token name = semanticStack.pop();
        ArrayDscp nameDscp = (ArrayDscp) SymboleTable.find(name);
        Token t = new Token(nameDscp.size, TokenType.integer);
        semanticStack.push(t);
    }

    public static void RECORDVAR() throws Exception {
        Token chum = semanticStack.pop();
        Token name = semanticStack.pop();
        Dscp nameDscp = SymboleTable.find(name);
        if (nameDscp.dscpType == DscpType.array && chum.getValue().equals("length")) {
            semanticStack.push(name);
            return;
        }

    }

    public static void NEG() throws Exception {

        if (token.getType() == TokenType.id || token.getType() == TokenType.real || token.getType() == TokenType.integer) {
            token.setValue(DecafScanner.value);
        }
        if (token.getType() == TokenType.id) {
            VariableDscp dscp = (VariableDscp) SymboleTable.find(token);
            switch (dscp.type.type) {
                case Integer:
                    mipsCode.add(new Code("lw", "$t4", dscp.addr + "($t0)"));
                    mipsCode.add(new Code("neg", "$t3", "$t4"));
                    mipsCode.add(new Code("sw", "$t3", dscp.addr + "($t0)"));
                    break;
                case Double:
                    mipsCode.add(new Code("ld", "$f0", dscp.addr + "($t0)"));
                    mipsCode.add(new Code("neg.d", "$t0", "$t2"));
                    mipsCode.add(new Code("sd", "$t0", dscp.addr + "($t0)"));
                    break;
                case String:
                case Boolean:
                case Record:
                case Array:
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("negetive sign can not be befor not number feild");
                    throw new Exception();


            }

        }
        if (token.getType() == TokenType.integer || token.getType() == TokenType.real) {
            token.setValue("-" + DecafScanner.value);
        }
        semanticStack.push(token);

    }

    public static void PUSH() {

        if (token.getType() == TokenType.id || token.getType() == TokenType.str_char || token.getType() == TokenType.real || token.getType() == TokenType.integer) {
            token.setValue(DecafScanner.value);
        }
        if (token.getValue().equals("colon"))
            token.setValue(",");
        if (token.getValue().equals("true")) {
            token.setValue("1");
            token.setType(TokenType.integer);
        }
        if (token.getValue().equals("false")) {
            token.setValue("0");
            token.setType(TokenType.integer);
        }
        semanticStack.push(token);
    }

    public static void MDSCP() throws Exception {
        Token varName = semanticStack.pop();
        Token chum = semanticStack.pop();
        if (symboleTables.get(symboleTables.size() - 1).get(varName) != null) {
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println(varName.getValue() + "is declared befor");
            //error
            throw new Exception();
        }
        Dscp dscp;
        if (chum.getValue().equals("]")) {
            Token type = semanticStack.pop();
            dscp = (ArrayDscp) new ArrayDscp(type.getValue());
            dscp.addr = address;
            address += 4;

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
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("size of array should be an integer number");
            //error
            throw new Exception();
        }

        ArrayDscp arrayDscp = new ArrayDscp(type.getValue());
        if (((VariableDscp) dSize).isImm)
            arrayDscp.size = ((VariableDscp) dSize).value;
        mipsCode.add(new Code("li", "$t3", String.valueOf(address)));
        mipsCode.add(new Code("sw", "$t3", temp + "($t1)"));
        arrayDscp.addr = temp;
        temp += 4;
        address += arrayDscp.type.size * Integer.parseInt(arrayDscp.size);
        Token t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, arrayDscp);
        semanticStack.push(t);

    }

    public static void ARRAY() throws Exception {
        Token index = semanticStack.pop();
        Token arrayName = semanticStack.pop();
        Dscp indexD = SymboleTable.find(index);
        Dscp arrD = SymboleTable.find(arrayName);
        if (arrD.addr == -1 || indexD.addr == -1) {
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declead array or index");
            //error
            throw new Exception();
        }
        if (arrD.dscpType != DscpType.array || indexD.dscpType != DscpType.variable) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println(arrayName.getValue() + " isnt an array or " + arrayName.getValue() + "isn't a variable");
            throw new Exception();
        }
        ArrayDscp arrDscp = (ArrayDscp) arrD;
        VariableDscp indexDscp = (VariableDscp) indexD;
        if (indexDscp.type.type != Type.Integer) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("index should be an integer number");
            throw new Exception();
        }
        VariableDscp cell = new VariableDscp(arrDscp.type, temp, false, true);
        temp += arrDscp.type.size;
        String base = indexDscp.isTemp ? "($t1)" : "($t0)";
        if (indexDscp.isImm)
            mipsCode.add(new Code("li", "$t3", indexDscp.value));
        else
            mipsCode.add(new Code("lw", "$t3", indexDscp.addr + base));
        mipsCode.add(new Code("li", "$t4", String.valueOf(arrDscp.type.size)));
        mipsCode.add(new Code("mult", "$t4", "$t3"));
        mipsCode.add(new Code("mflo", "$t3")); // index * size
        mipsCode.add(new Code("lw", "$t4", arrDscp.addr + "($t0)"));
        mipsCode.add(new Code("add", "$t3", "$t3", "$t4")); // baseArr + index * size
        mipsCode.add(new Code("sw", "$t3", temp + "($t1)"));
        cell.refAddress = temp;
        temp += 4;
        mipsCode.add(new Code("add", "$t3", "$t3", "$t0"));
        if (arrDscp.type.type == Type.Double) {
            mipsCode.add(new Code("l.d", "$f0", "0($t3)"));
            mipsCode.add(new Code("s.d", "$f0", cell.addr + "($t1)"));
        } else {
            mipsCode.add(new Code("lw", "$t4", "0($t3)"));
            mipsCode.add(new Code("sw", "$t4", cell.addr + "($t1)"));
        }
        Token t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, cell);
        semanticStack.push(t);
    }

    public static void ADD() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            //error
            throw new Exception();
        }
        VarType type = getType(d1, d2);
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
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }
        VarType type = getType(d1, d2);
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
                        mipsCode.add(new Code("li.d", "$f0", String.valueOf(Double.parseDouble(String.valueOf(d1.value)))));
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
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }
        VarType type = getType(d1, d2);
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
                        mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
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
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }
        VarType type = getType(d1, d2);
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

    public static void LT() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }

        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
        temp += d.type.size;

        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        Token t;
        switch (d1.type.type) {
            case Integer:
            case Boolean:
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {
                    if (d1.isImm && d2.isImm) {
                        String s = Integer.parseInt(d2.value) < Integer.parseInt(d1.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else if (d1.isImm) {
                        mipsCode.add(new Code("lw", "$t3", d2.addr + s2));
                        mipsCode.add(new Code("slti", "$t4", "$t3", d1.value));
                        mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));

                    } else if (d2.isImm) {
                        mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                        mipsCode.add(new Code("li", "$t4", d2.value));
                        mipsCode.add(new Code("slt", "$t4", "$t4", "$t3"));
                        mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));
                    } else {
                        mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                        mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                        mipsCode.add(new Code("slt", "$t4", "$t4", "$t3"));
                        mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));
                    }
                } else if (d2.type.type == Type.Double) {
                    if (d1.isImm && d2.isImm) {
                        String s = Double.parseDouble(d2.value) < Double.parseDouble(d2.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else {

                        castIntToDouble(d1, s1, "$f0");

                        if (d2.isImm) {
                            mipsCode.add(new Code("li.d", "$f2", d2.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                        }
                        mipsCode.add(new Code("c.lt.d", "$f2", "$f0"));
                        doubleCompare("$t3", "f");
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                        d.type = new VarType(Type.Double);
                    }
                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't compare this types");
                    throw new Exception();
                }
                break;
            case Double:
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {
                    if (d1.isImm && d2.isImm) {
                        String s = Double.parseDouble(d2.value) < Double.parseDouble(d2.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else {

                        if (d1.isImm) {
                            mipsCode.add(new Code("li.d", "$f0", d1.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        }

                        castIntToDouble(d2, s2, "$f2");

                        mipsCode.add(new Code("c.lt.d", "$f2", "$f0"));
                        doubleCompare("$t3", "f");
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                        d.type = new VarType(Type.Double);
                    }

                } else if (d2.type.type == Type.Double) {
                    if (d1.isImm && d2.isImm) {
                        String s = Double.parseDouble(d2.value) < Double.parseDouble(d2.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else {
                        if (d1.isImm) {
                            mipsCode.add(new Code("li.d", "$f0", d1.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        }
                        if (d2.isImm) {
                            mipsCode.add(new Code("li.d", "$f2", d2.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                        }
                        mipsCode.add(new Code("c.lt.d", "$f2", "$f0"));
                        doubleCompare("$t3", "f");
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                        d.type = new VarType(Type.Double);
                    }
                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't compare this types");
                    throw new Exception();
                }
                break;
            default:
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("can't compare this types");
                throw new Exception();
        }


        t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void LE() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }

        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
        temp += d.type.size;

        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        Token t;
        switch (d1.type.type) {
            case Integer:
            case Boolean:
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {
                    if (d1.isImm && d2.isImm) {
                        String s = Integer.parseInt(d2.value) < Integer.parseInt(d1.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else {
                        if (d1.isImm) {
                            mipsCode.add(new Code("li", "$t3", d1.value));
                        } else {
                            mipsCode.add(new Code("lw", "$t3", d1.addr + s1));


                        }
                        if (d2.isImm) {
                            mipsCode.add(new Code("li", "$t4", d2.value));
                        } else {
                            mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                        }

                        mipsCode.add(new Code("sle", "$t4", "$t4", "$t3"));
                        mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));

                    }
                } else if (d2.type.type == Type.Double) {
                    if (d1.isImm && d2.isImm) {
                        String s = Double.parseDouble(d2.value) < Double.parseDouble(d2.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else {

                        castIntToDouble(d1, s1, "$f0");

                        if (d2.isImm) {
                            mipsCode.add(new Code("li.d", "$f2", d2.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                        }
                        mipsCode.add(new Code("c.le.d", "$f2", "$f0"));
                        doubleCompare("$t3", "f");
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                        d.type = new VarType(Type.Double);
                    }
                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't compare this types");
                    throw new Exception();
                }
                break;
            case Double:
                if (d2.type.type == Type.Integer || d2.type.type == Type.Boolean) {
                    if (d1.isImm && d2.isImm) {
                        String s = Double.parseDouble(d2.value) < Double.parseDouble(d2.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else {

                        if (d1.isImm) {
                            mipsCode.add(new Code("li.d", "$f0", d1.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        }

                        castIntToDouble(d2, s2, "$f2");

                        mipsCode.add(new Code("c.le.d", "$f2", "$f0"));
                        doubleCompare("$t3", "f");
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                        d.type = new VarType(Type.Double);
                    }

                } else if (d2.type.type == Type.Double) {
                    if (d1.isImm && d2.isImm) {
                        String s = Double.parseDouble(d2.value) < Double.parseDouble(d2.value) ? "1" : "0";
                        mipsCode.add(new Code("li", "$t3", s));
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                    } else {
                        if (d1.isImm) {
                            mipsCode.add(new Code("li.d", "$f0", d1.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                        }
                        if (d2.isImm) {
                            mipsCode.add(new Code("li.d", "$f2", d2.value));
                        } else {
                            mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                        }
                        mipsCode.add(new Code("c.le.d", "$f2", "$f0"));
                        doubleCompare("$t3", "f");
                        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                        d.type = new VarType(Type.Double);
                    }
                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't compare this types");
                    throw new Exception();
                }
                break;
            default:
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("can't compare this types");
                throw new Exception();
        }


        t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void GE() throws Exception {
        Token first = semanticStack.pop();
        Token second = semanticStack.pop();
        semanticStack.push(first);
        semanticStack.push(second);
        LE();
    }

    public static void GT() throws Exception {
        Token first = semanticStack.pop();
        Token second = semanticStack.pop();
        semanticStack.push(first);
        semanticStack.push(second);
        LT();
    }

    public static void EQUAL() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }
        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
        temp += d.type.size;

        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        Token t;

        if (d1.type.type == Type.Integer && d2.type.type == Type.Integer ||
                d1.type.type == Type.Boolean && d2.type.type == Type.Boolean) {

            if (d1.isImm && d2.isImm) {
                String s = d1.value.equals(d2.value) ? "1" : "0";
                mipsCode.add(new Code("li", "$t3", s));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("li", "$t3", d1.value));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                }
                if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t4", d2.value));
                } else {
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                }
                mipsCode.add(new Code("seq", "$t4", "$t4", "$t3"));
                mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));
            }
        } else if (d1.type.type == Type.Double && d2.type.type == Type.Double) {

            if (d1.isImm && d2.isImm) {
                String s = Double.parseDouble(d2.value) == Double.parseDouble(d2.value) ? "1" : "0";
                mipsCode.add(new Code("li", "$t3", s));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("li.d", "$f0", d1.value));
                } else {
                    mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                if (d2.isImm) {
                    mipsCode.add(new Code("li.d", "$f2", d2.value));
                } else {
                    mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                }
                mipsCode.add(new Code("c.eq.d", "$f2", "$f0"));
                doubleCompare("$t3", "f");
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                d.type = new VarType(Type.Double);
            }
        } else if (d1.type.type == Type.String && d2.type.type == Type.String) {
            String s;
            if (d1.isImm && d2.isImm) {
                s = (d1.value.equals(d2.value)) ? "1" : "0";
            } else {
                s = (d1.addr == d2.addr) ? "1" : "0";
            }
            mipsCode.add(new Code("li", "$t3", s));
            mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));

        } else if (d1.type.type == Type.Record && d2.type.type == Type.Record) {
            String s = (d1.addr == d2.addr) ? "1" : "0";
            mipsCode.add(new Code("li", "$t3", s));
            mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
        } else if (d1.type.type == Type.Array && d2.type.type == Type.Array) {
            String s = (d1.addr == d2.addr) ? "1" : "0";
            mipsCode.add(new Code("li", "$t3", s));
            mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
        } else {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("can't compare this types");
            throw new Exception();
        }

        t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void NOTEQUAL() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");

            throw new Exception();
        }
        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
        temp += d.type.size;

        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        Token t;

        if (d1.type.type == Type.Integer && d2.type.type == Type.Integer ||
                d1.type.type == Type.Boolean && d2.type.type == Type.Boolean) {

            if (d1.isImm && d2.isImm) {
                String s = d1.value.equals(d2.value) ? "0" : "1";
                mipsCode.add(new Code("li", "$t3", s));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("li", "$t3", d1.value));
                } else {
                    mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                }
                if (d2.isImm) {
                    mipsCode.add(new Code("li", "$t4", d2.value));
                } else {
                    mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                }
                mipsCode.add(new Code("sne", "$t4", "$t4", "$t3"));
                mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));
            }
        } else if (d1.type.type == Type.Double && d2.type.type == Type.Double) {

            if (d1.isImm && d2.isImm) {
                String s = Double.parseDouble(d2.value) == Double.parseDouble(d2.value) ? "0" : "1";
                mipsCode.add(new Code("li", "$t3", s));
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
            } else {
                if (d1.isImm) {
                    mipsCode.add(new Code("li.d", "$f0", d1.value));
                } else {
                    mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                }
                if (d2.isImm) {
                    mipsCode.add(new Code("li.d", "$f2", d2.value));
                } else {
                    mipsCode.add(new Code("l.d", "$f2", d2.addr + s2));
                }
                mipsCode.add(new Code("c.eq.d", "$f2", "$f0"));
                doubleCompare("$t3", "t");
                mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
                d.type = new VarType(Type.Double);
            }
        } else if (d1.type.type == Type.String && d2.type.type == Type.String) {
            String s;
            if (d1.isImm && d2.isImm) {
                s = (d1.value.equals(d2.value)) ? "0" : "1";
            } else {
                s = (d1.addr == d2.addr) ? "0" : "1";
            }
            mipsCode.add(new Code("li", "$t3", s));
            mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));

        } else if (d1.type.type == Type.Record && d2.type.type == Type.Record) {
            String s = (d1.addr == d2.addr) ? "0" : "1";
            mipsCode.add(new Code("li", "$t3", s));
            mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
        } else if (d1.type.type == Type.Array && d2.type.type == Type.Array) {
            String s = (d1.addr == d2.addr) ? "0" : "1";
            mipsCode.add(new Code("li", "$t3", s));
            mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));
        } else {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("can't compare this types");
            throw new Exception();
        }

        t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void AND() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }

        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
        temp += d.type.size;

        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        Token t;
        if (!(((d1.type.type == Type.Boolean) || (d1.type.type == Type.Integer) || (d1.type.type == Type.Double)) &&
                ((d2.type.type == Type.Boolean) || (d2.type.type == Type.Integer) || (d2.type.type == Type.Double)))) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("can't compare this types");
            throw new Exception();
        }

        if (d1.isImm) {
            castImmToBool(src1, "$t3");
        } else {
            if (d1.type.type == Type.Double) {
                mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                castVariabelToBool(d1, s1, "$t3", "$f0");
            } else {
                mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                if (d1.type.type == Type.Integer) {
                    castVariabelToBool(d1, s1, "$t3", "$t3");
                }
            }
        }
        if (d2.isImm) {
            castImmToBool(src2, "$t4");
        } else {
            if (d2.type.type == Type.Double) {
                mipsCode.add(new Code("l.d", "$f0", d2.addr + s2));
                castVariabelToBool(d2, s2, "$t4", "$f0");
            } else {
                mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                if (d2.type.type == Type.Integer) {
                    castVariabelToBool(d2, s2, "$t4", "$t4");
                }
            }
        }

        mipsCode.add(new Code("and", "$t4", "$t4", "$t3"));
        mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));


        t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void OR() throws Exception {

        Token src1 = semanticStack.pop();
        Token src2 = semanticStack.pop();
        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);
        VariableDscp d2 = (VariableDscp) SymboleTable.find(src2);
        if (d1.addr == -1 || d2.addr == -1) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }

        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
        temp += d.type.size;

        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        String s2 = (d2.isTemp ? "($t1)" : "($t0)");
        Token t;
        if (!(((d1.type.type == Type.Boolean) || (d1.type.type == Type.Integer) || (d1.type.type == Type.Double)) &&
                ((d2.type.type == Type.Boolean) || (d2.type.type == Type.Integer) || (d2.type.type == Type.Double)))) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("can't compare this types");
            throw new Exception();
        }

        if (d1.isImm) {
            castImmToBool(src1, "$t3");
        } else {
            if (d1.type.type == Type.Double) {
                mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                castVariabelToBool(d1, s1, "$t3", "$f0");
            } else {
                mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                if (d1.type.type == Type.Integer) {
                    castVariabelToBool(d1, s1, "$t3", "$t3");
                }
            }
        }
        if (d2.isImm) {
            castImmToBool(src2, "$t4");
        } else {
            if (d2.type.type == Type.Double) {
                mipsCode.add(new Code("l.d", "$f0", d2.addr + s2));
                castVariabelToBool(d2, s2, "$t4", "$f0");
            } else {
                mipsCode.add(new Code("lw", "$t4", d2.addr + s2));
                if (d2.type.type == Type.Integer) {
                    castVariabelToBool(d2, s2, "$t4", "$t4");
                }
            }
        }

        mipsCode.add(new Code("or", "$t4", "$t4", "$t3"));
        mipsCode.add(new Code("sw", "$t4", d.addr + "($t1)"));


        t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void NOT() throws Exception {

        Token src1 = semanticStack.pop();

        VariableDscp d1 = (VariableDscp) SymboleTable.find(src1);

        if (d1.addr == -1) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            throw new Exception();
        }

        VariableDscp d = new VariableDscp(new VarType(Type.Boolean), temp, false, true);
        temp += d.type.size;

        String s1 = (d1.isTemp ? "($t1)" : "($t0)");
        Token t;

        if (!((d1.type.type == Type.Boolean) || (d1.type.type == Type.Integer) || (d1.type.type == Type.Double))) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("can't compare this types");
            throw new Exception();
        }

        if (d1.isImm) {
            castImmToBool(src1, "$t3");
        } else {
            if (d1.type.type == Type.Double) {
                mipsCode.add(new Code("l.d", "$f0", d1.addr + s1));
                castVariabelToBool(d1, s1, "$t3", "$f0");
            } else {
                mipsCode.add(new Code("lw", "$t3", d1.addr + s1));
                if (d1.type.type == Type.Integer) {
                    castVariabelToBool(d1, s1, "$t3", "$t3");
                }
            }
        }

        mipsCode.add(new Code("not", "$t3", "$t3"));
        mipsCode.add(new Code("sw", "$t3", d.addr + "($t1)"));

        t = new Token("$" + mipsCode.size(), TokenType.id);
        symboleTables.get(symboleTables.size() - 1).add(t, d);
        semanticStack.push(t);
    }

    public static void READINTEGER() throws Exception {
        VariableDscp d = new VariableDscp(new VarType(Type.Integer), temp, false, true);
        Token t = new Token("$" + mipsCode.size(), TokenType.id);
        temp += d.type.size;
        mipsCode.add(new Code("li", "$v0", "5"));
        mipsCode.add(new Code("syscall"));
        mipsCode.add(new Code("sw", "$v0", d.addr + "($t1)"));
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
        mipsCode.add(new Code("la", "$a0", "buffer"));
        mipsCode.add(new Code("li", "$a1", "64"));
        mipsCode.add(new Code("sw", "$a0", d.addr + "($t0)"));
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
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
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
                mipsCode.add(new Code("l.d", "$f12", "0" + base));
                break;
            case String:
                if (d.isImm) {
                    mipsCode.add(new Code("la", "$a0", "str" + d.stringAddress));


                } else {
                    mipsCode.add(new Code("lw", "$t3", d.addr + "($t1)"));
                    mipsCode.add(new Code("add", "$t3", "$t0", "$t3"));
                    mipsCode.add(new Code("lw", "$a0", "0($t3)"));

                }
                funcNum = "4";


        }

        mipsCode.add(new Code("li", "$v0", funcNum));
        mipsCode.add(new Code("syscall"));
        mipsCode.add(new Code("li", "$v0", "4"));
        mipsCode.add(new Code("la", "$a0", "nl"));
        mipsCode.add(new Code("syscall"));

    }

    public static void ASSIGN() throws Exception {
        Token right = semanticStack.pop();
        Token left = semanticStack.pop();
        Dscp dRight = find(right);
        Dscp dLeft = find(left);
        String base;
        String baseL = "($t0)";
        if (dRight.addr == -1) {
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("not declared reference");
            //error
            throw new Exception();
        }
        if (dLeft.dscpType == DscpType.variable && dRight.dscpType == DscpType.variable) {

            VariableDscp dR = (VariableDscp) dRight;
            VariableDscp dL = (VariableDscp) dLeft;

            if (dL.isImm) { //?
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("left parameter of assign can't be immediate");
                throw new Exception();
            }
            if (dL.isTemp) {
                if (dL.refAddress == -1) {//error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("not declared reference");
                    throw new Exception();
                } else {
                    mipsCode.add(new Code("lw", "$t8", dL.refAddress + "($t1)"));
                    mipsCode.add(new Code("add", "$t8", "$t8", "$t0"));
                    baseL = "($t8)";
                    dL.addr = 0;
                }

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
                        mipsCode.add(new Code("sw", "$t3", dL.addr + baseL));
                        break;

                    case Double:
                        base = dR.isTemp ? "($t1)" : "($t0)";
                        castDoubleToInt(dR, base, "$t3");
                        mipsCode.add(new Code("sw", "$t3", dL.addr + baseL));
                        break;

                    default:
                        //error
                        System.out.println("line " + scanner.lineNum + " :");
                        System.out.println("can't assign " + dR.type.type.toString() + " to integer");
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
                    mipsCode.add(new Code("s.d", "$f0", dL.addr + baseL));

                } else if (dR.type.type == Type.Integer) {
                    base = dR.isTemp ? "($t1)" : "($t0)";
                    castIntToDouble(dR, base, "$f0");
                    mipsCode.add(new Code("s.d", "$f0", dL.addr + baseL));

                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't assign " + dR.type.type.toString() + " to double");
                    throw new Exception();
                }
            } else if (dL.type.type == Type.Boolean) {
                base = dR.isTemp ? "($t1)" : "($t0)";
                if (dR.type.type == Type.Boolean) {

                    mipsCode.add(new Code("lw", "$t3", dR.addr + base));
                    mipsCode.add(new Code("sw", "$t3", dL.addr + baseL));

                } else if (dR.type.type == Type.Integer) {
                    if (dR.isImm) {
                        castImmToBool(right, "$t3");
                    } else {
                        castVariabelToBool(dR, base, "$t3", "$t4");
                    }
                    mipsCode.add(new Code("sw", "$t3", dL.addr + baseL));

                } else if (dR.type.type == Type.Double) {
                    if (dR.isImm) {
                        castImmToBool(right, "$f0");
                    } else {
                        castVariabelToBool(dR, base, "$f0", "$f2");
                    }
                    mipsCode.add(new Code("sw", "$f0", dL.addr + baseL));

                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't assign " + dR.type.type.toString() + " to boolean");
                    throw new Exception();
                }
            } else if (dL.type.type == Type.Record) {

                if (dR.type.type == Type.Record && typeSetter(dR.refType) == typeSetter(dL.refType)) {
                    dL.addr = dR.addr;
                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't assign " + dR.type.type.toString() + " to record");
                    throw new Exception();
                }
            } else if (dL.type.type == Type.String) {
                if (dR.type.type == Type.String) {
                    dL.addr = dR.addr;
                } else {
                    //error
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("can't assign " + dR.type.type.toString() + " to string");
                    throw new Exception();
                }
            } else {
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("can't use assign for " + dL.type.type.toString());
                throw new Exception();
            }

        } else if (dLeft.dscpType == DscpType.array && dRight.dscpType == DscpType.array &&
                ((ArrayDscp) dLeft).type.equals(((ArrayDscp) dRight).type)) {
            ArrayDscp dR = (ArrayDscp) dRight;
            ArrayDscp dL = (ArrayDscp) dLeft;
            dL.addr = dR.addr;

        } else {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("can't use assign for " + dLeft.dscpType);
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
        semanticStack.push(new Token(String.valueOf(mipsCode.size()), TokenType.pc));

        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void JZ() throws Exception {

        Token token = semanticStack.pop();
        if (token.getType() == TokenType.undefined) {
            //error
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("use return value of a void function");
            throw new Exception();
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmToBool(token, "$t3");
            mipsCode.add(new Code("beqz", "$t3"));
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            if (varBe.addr == -2) {
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("not declared reference");
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

                mipsCode.add(new Code("lw", "$t3", varBe.addr + src));
                mipsCode.add(new Code("beqz", "$t3"));

            } else if (varBe.type.type == Type.Double) {
                if (varBe.isTemp) {
                    mipsCode.add(new Code("bc1f"));

                } else {
                    mipsCode.add(new Code("l.d", "$f2", varBe.addr + src));
                    mipsCode.add(new Code("li.d", "$f0", "0.0"));
                    mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                    mipsCode.add(new Code("bc1t"));
                }
            } else {
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("not suitable type for condition");
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
            mipsCode.get(Integer.parseInt(breakPC.getValue())).dest = "L" + labelNum;
            jzPC = semanticStack.pop();
            while (jzPC.getType() == TokenType.undefined) {
                jzPC = semanticStack.pop();
            }
        }
        if (mipsCode.get(Integer.parseInt(jzPC.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).dest = "L" + labelNum;
        } else {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).src1 = "L" + labelNum;
        }
        Token whilePC = semanticStack.pop();
        String s = mipsCode.get(Integer.parseInt(whilePC.getValue())).dest;
        mipsCode.add(new Code("b", (s).substring(0, s.length() - 1)));
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void CJZ() {
        Token jzPC = semanticStack.pop();
        ArrayList<Token> tokens = new ArrayList<>();
        while (jzPC.getValue().equals("break")) {
            tokens.add(jzPC);
            tokens.add(semanticStack.pop());
            jzPC = semanticStack.pop();
        }

        if (mipsCode.get(Integer.parseInt(jzPC.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).dest = "L" + labelNum;
        } else {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).src1 = "L" + labelNum;
        }
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        for (int i = tokens.size() - 1; i >= 0; i--) {
            semanticStack.push(tokens.get(i));
        }
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
        ArrayList<Token> tokens = new ArrayList<>();
        Token jpPC = semanticStack.pop();
        while (jpPC.getValue().equals("break")) {
            tokens.add(jpPC);
            tokens.add(semanticStack.pop());
            jpPC = semanticStack.pop();
        }
        mipsCode.get(Integer.parseInt(jpPC.getValue())).dest = "L" + labelNum;
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        for (int i = tokens.size() - 1; i >= 0; i--) {
            semanticStack.push(tokens.get(i));
        }
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
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("use return value of a void function");
            throw new Exception();
        }
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmToBool(token, "$t3");
            mipsCode.add(new Code("beqz", "$t3"));
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            if (varBe.addr == -1) {
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("not declared reference");
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
                if (varBe.isTemp) {
                    mipsCode.add(new Code("bc1f"));

                } else {
                    mipsCode.add(new Code("l.d", "$f2", varBe.addr + src));
                    mipsCode.add(new Code("li.d", "$f0", "0.0"));
                    mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                    mipsCode.add(new Code("bc1t"));
                }
            } else {
                //error? :/
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("not suitable type for condition");
                throw new Exception();
            }
        }
        semanticStack.push(new Token(String.valueOf(mipsCode.size() - 1), TokenType.pc));
        if (token.getType() == TokenType.keyword || token.getType() == TokenType.integer || token.getType() == TokenType.real || token.getType() == TokenType.str_char) {
            castImmToBool(token, "$t3");
            mipsCode.add(new Code("bnqz", "$t3"));
        } else if (token.getType() == TokenType.id) {
            VariableDscp varBe = (VariableDscp) SymboleTable.find(token);
            if (varBe.addr == -1) {
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("not declared reference");
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
                if (varBe.isTemp) {
                    mipsCode.add(new Code("bc1t"));

                } else {
                    mipsCode.add(new Code("l.d", "$f2", varBe.addr + src));
                    mipsCode.add(new Code("li.d", "$f0", "0.0"));
                    mipsCode.add(new Code("c.eq.d", "$f0", "$f2"));
                    mipsCode.add(new Code("bc1f"));
                }
            } else {

                //error? :/
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("not suitable type for condition");
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
        String s = mipsCode.get(Integer.parseInt(sbe.getValue())).dest;
        mipsCode.add(new Code("b", s.substring(0, s.length() - 1)));
        if (mipsCode.get(Integer.parseInt(jnz.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jnz.getValue())).dest = "L" + labelNum;
        } else {
            mipsCode.get(Integer.parseInt(jnz.getValue())).src1 = "L" + labelNum;
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
            mipsCode.get(Integer.parseInt(breakPC.getValue())).dest = "L" + labelNum;
            sstep = semanticStack.pop();
            while (sstep.getType() == TokenType.undefined) {
                sstep = semanticStack.pop();
            }
        }
        String s = mipsCode.get(Integer.parseInt(sstep.getValue())).dest;
        mipsCode.add(new Code("b", s.substring(0, s.length() - 1)));
        Token jz = semanticStack.pop();
        if (mipsCode.get(Integer.parseInt(jz.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jz.getValue())).dest = "L" + labelNum;
        } else {
            mipsCode.get(Integer.parseInt(jz.getValue())).src1 = "L" + labelNum;
        }
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
    }

    public static void FUNCTION() throws Exception {
        if (!token.getValue().equals("main"))
            token.setValue(DecafScanner.value);

        Token name = token;
        Token returnType = semanticStack.pop();
        if (symboleTables.get(0).get(name) != null) {
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("duplicate name function");
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
        if (name.getValue().equals("main")) {
            mipsCode.add(new Code("la", "$t0", "address"));
            mipsCode.add(new Code("la", "$t1", "temp"));
        }
    }

    public static void ADDPARAM() throws Exception {
        int size = 0;
        Token inputName = semanticStack.pop();
        Token type = semanticStack.pop();
        if (symboleTables.get(symboleTables.size() - 1).get(inputName) != null) {
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println("duplicate input name for function");
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
                    System.out.println("line " + scanner.lineNum + " :");
                    System.out.println("not deaclared record type");
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

    public static void ENDFUNCTION() throws Exception {
        Token token = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp dscp = (FuncDscp) symboleTables.get(0).get(token);
        if (!dscp.returnType.equals("void") && !dscp.hasReturn && !token.getValue().equals("main")) {
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println(token.getValue() + "function need return type");
            throw new Exception();
        }
        if (dscp.returnType.equals("void") && !dscp.hasReturn) {
            mipsCode.add(new Code("jr", "$ra"));
        }
        if (temp > maxTemp)
            maxTemp = temp;
        temp = 0;
        symboleTables.remove(symboleTables.size() - 1);
    }

    public static void RETURNP() throws Exception {
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        Type type = null;

        if (funcDscp.returnType.equals("void")) {
            //error ->void
            System.out.println("line " + scanner.lineNum + " :");
            System.out.println(funcName.getValue() + "function is void don't need return value");
            throw new Exception();
        }
        Token chum = semanticStack.pop();
        Dscp dscp = SymboleTable.find(chum);
        switch (dscp.dscpType) {
            case funcion:
            case record:
            case array:
                //error
                System.out.println("line " + scanner.lineNum + " :");
                System.out.println("return type isn't ok for " + funcName.getValue());
                throw new Exception();

            case variable:
                type = typeSetter(funcDscp.returnType).type;
                switch (((VariableDscp) dscp).type.type) {
                    case Integer:
                    case Double:
                    case Boolean:
                        if (!(type == Type.Integer || type == Type.Double || type == Type.Boolean)) {
                            //error
                            System.out.println("line "+scanner.lineNum+" :");
                            System.out.println("return type is incorrect and can't be casted for" + funcName.getValue());
                            throw new Exception();
                        }
                        break;
                    case String:
                        if (!(type == Type.String || type == Type.Boolean)) {
                            //error
                            System.out.println("line "+scanner.lineNum+" :");
                            System.out.println("return type is incorrect and can't be casted for" + funcName.getValue());
                            throw new Exception();
                        }
                        break;
                    case Record:
                        if (((VariableDscp) dscp).refType != funcDscp.returnType) {
                            //error
                            System.out.println("line "+scanner.lineNum+" :");
                            System.out.println("return type is incorrect and can't be casted for" + funcName.getValue());
                            throw new Exception();
                        }
                        break;

                }
                break;
        }
        ((FuncDscp) symboleTables.get(0).get(funcName)).hasReturn = true;
        VariableDscp variableDscp = (VariableDscp) dscp;
        String base = variableDscp.isTemp ? "($t1)" : "($t0)";
        if (type == Type.Double) {
            mipsCode.add(new Code("addi", "$sp", "$sp", "-8"));
            if (variableDscp.type.type == Type.Double) {
                if (variableDscp.isImm) {
                    mipsCode.add(new Code("li.d", "$f0", variableDscp.value));
                } else {
                    mipsCode.add(new Code("l.d", "$f0", variableDscp.addr + base));

                }
            } else if (variableDscp.type.type == Type.Integer || variableDscp.type.type == Type.Boolean) {
                castIntToDouble(variableDscp, base, "$f0");
            }
            mipsCode.add(new Code("s.d", "$f0", 0 + "($sp)"));

        } else if (type == Type.Integer) {
            mipsCode.add(new Code("addi", "$sp", "$sp", "-4"));
            if (variableDscp.type.type == Type.Integer || variableDscp.type.type == Type.Boolean) {
                if (variableDscp.isImm) {
                    mipsCode.add(new Code("li", "$t3", variableDscp.value));
                } else {
                    mipsCode.add(new Code("lw", "$t3", variableDscp.addr + base));

                }
            } else if (variableDscp.type.type == Type.Double) {
                castDoubleToInt(variableDscp, base, "$t3");
            }
            mipsCode.add(new Code("sw", "$t3", 0 + "($sp)"));
        } else if (type == Type.Boolean) {
            if (variableDscp.isImm) {
                castImmToBool(chum, "$t3");
            } else {
                if (variableDscp.type.type == Type.Double)
                    castVariabelToBool(variableDscp, base, "$t3", "$f0");
                else if (variableDscp.type.type == Type.Integer) {
                    castVariabelToBool(variableDscp, base, "$t3", "$t4");
                } else {
                    mipsCode.add(new Code("lw", "$t3", variableDscp.addr + base));

                }
            }
            mipsCode.add(new Code("sw", "$t3", 0 + "($sp)"));

        } else if (type == Type.String) {

        } else if (type == Type.Record) {

        }
        mipsCode.add(new Code("jr", "$ra"));
    }

    public static void RETURNNP() throws Exception {
        Token funcName = symboleTables.get(symboleTables.size() - 1).name;
        FuncDscp funcDscp = (FuncDscp) symboleTables.get(0).get(funcName);
        if (!funcDscp.returnType.equals("void")) {
            //error
            System.out.println("line "+scanner.lineNum+" :");
            System.out.println(funcName.getValue() + " isn't void and need return value");
            throw new Exception();
        } else {
            mipsCode.add(new Code("jr", "$ra"));
        }

    }

    public static void STARTFUNC() throws Exception {
        Token funcName = semanticStack.pop();
        Dscp funcDscp = symboleTables.get(0).get(funcName);
        if (funcDscp == null || funcDscp.dscpType != DscpType.funcion) {
            //error
            System.out.println("line "+scanner.lineNum+" :");
            System.out.println(funcName.getValue() + "is not decleard");
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
            System.out.println("line "+scanner.lineNum+" :");
            System.out.println(funcName.getValue() + "need input argument");
            throw new Exception();
        }
        int add = mipsCode.size();
        mipsCode.add(new Code("addi", "$sp", "$sp"));
        int a = storeNotTempInSP(symboleTables.get(symboleTables.size() - 1), 4);
        mipsCode.get(add).src2 = "-" + (a + 4);
        mipsCode.add(new Code("sw", "$ra", "0($sp)"));
        mipsCode.add(new Code("jal", funcName.getValue()));
        int size;
        int ra = 0;
        if (funcDscp.returnType.equals("void")) {
            semanticStack.push(new Token("void", TokenType.undefined));
            size = loadNotTempInSP(symboleTables.get(symboleTables.size() - 1), 4);
        } else {
            VarType type = typeSetter(funcDscp.returnType);
            size = loadNotTempInSP(symboleTables.get(symboleTables.size() - 1), 4 + type.size);
            if (type.type != Type.Record) {
                variableDscp = new VariableDscp(type, temp, false, true);
                if (type.type == Type.Double) {
                    mipsCode.add(new Code("l.d", "$f0", 8 + "($sp)"));
                    mipsCode.add(new Code("s.d", "$f0", temp + "($t1)"));
                    temp += 8;
                    ra = 8;
                } else {
                    mipsCode.add(new Code("lw", "$t3", 4 + "($sp)"));
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
        mipsCode.add(new Code("addi", "$sp", "$sp", String.valueOf(ra + 4 + size)));

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
            System.out.println("line "+scanner.lineNum+" :");
            System.out.println("input number doesn't match function inputs");
            throw new Exception();
        }
        int Sbase = funcDscp.size;
        int add = mipsCode.size();
        mipsCode.add(new Code("addi", "$sp", "$sp"));
        int a = storeNotTempInSP(symboleTables.get(symboleTables.size() - 1), Sbase);
        mipsCode.get(add).src2 = "-" + (a + Sbase);
        Sbase -= 4;
        mipsCode.add(new Code("sw", "$ra", Sbase + "($sp)"));
        for (int i = tokens.size() - 1; i >= 0; i--) {
            System.out.println(funcDscp.inputNames.get(tokens.size() - i - 1).getValue());
            Dscp funIn = funcDscp.symboleTable.get(funcDscp.inputNames.get(tokens.size() - i - 1));
            Dscp in = SymboleTable.find(tokens.get(i));
            if (in.addr == -1) {
                //error
                System.out.println("line "+scanner.lineNum+" :");
                System.out.println("not declared refrence");
                throw new Exception();
            }
            if (funIn.dscpType != in.dscpType) {
                //error
                System.out.println("line "+scanner.lineNum+" :");
                System.out.println("input type doesn't match function input type");
                throw new Exception();
            }
            if (in.dscpType == DscpType.funcion || in.dscpType == DscpType.record) {
                //error
                System.out.println("line "+scanner.lineNum+" :");
                System.out.println("can't pass "+in.dscpType.toString()+" to function");
                throw new Exception();
            } else if (in.dscpType == DscpType.array) {
                if (((ArrayDscp) funIn).type.type != ((ArrayDscp) in).type.type) {
                    //error
                    System.out.println("line "+scanner.lineNum+" :");
                    System.out.println("input type doesn't match function input type");
                    throw new Exception();
                } else {
                    mipsCode.add(new Code("lw", "$t3", in.addr + "($t0)"));
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
                        System.out.println("line "+scanner.lineNum+" :");
                        System.out.println("can't cast "+ var.type.type.toString()+" to boolean");
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
                        System.out.println("line "+scanner.lineNum+" :");
                        System.out.println("can't cast "+ var.type.type.toString()+" to integer");
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
                        System.out.println("line "+scanner.lineNum+" :");
                        System.out.println("can't cast "+ var.type.type.toString()+" to double");
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
        int size = 0;
        if (funcDscp.returnType.equals("void")) {
            semanticStack.push(new Token("void", TokenType.undefined));
            size = loadNotTempInSP(symboleTables.get(symboleTables.size() - 1), 4);
        } else {

            VarType type = typeSetter(funcDscp.returnType);
            if (type.type != Type.Record) {
                size = loadNotTempInSP(symboleTables.get(symboleTables.size() - 1), 4 + type.size);
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
        mipsCode.add(new Code("addi", "$sp", "$sp", String.valueOf(ra + 4 + size)));

    }

    public static void STARTBLOCK() {
        symboleTables.add(new SymboleTable());
    }

    public static void ENDBLOCK() {
        symboleTables.remove(symboleTables.size() - 1);
        if (temp > maxTemp)
            maxTemp = temp;
        temp = 0;
    }

}
