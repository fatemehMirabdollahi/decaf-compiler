package codegen;

import codegen.discriptors.Dscp;
import codegen.discriptors.Type;
import codegen.discriptors.VariableDscp;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import javafx.embed.swt.SWTFXUtils;
import parser.Parser;
import scanner.Token;
import scanner.TokenType;

import static codegen.SymboleTable.*;
import static codegen.SymboleTable.symboleTables;
import static parser.Parser.*;

public class CodeGen {
    public static void PUSH() {
        semanticStack.push(token);
    }

    public static void cast(Dscp dscp, String type) {
    }

    //    public static void ADD(){
//        Token src1 = semanticStack.pop();
//        Token src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "Arith");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("add",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
//
//    public static void SUB(){
//        String src1 = semanticStack.pop();
//        String src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "Arith");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("sub",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
//
//    public static void MUL(){
//        String src1 = semanticStack.pop();
//        String src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "Arith");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("mul",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
//
//    public static void DIV(){
//        String src1 = semanticStack.pop();
//        String src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "Arith");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("div",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
//
//    public static void LT(){
//        String src1 = semanticStack.pop();
//        String src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "compare");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("lessThan",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
//
//    public static void LE(){
//        String src1 = semanticStack.pop();
//        String src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "compare");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("lessEqual",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
//
//    public static void GT(){
//        String src1 = semanticStack.pop();
//        String src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "compare");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("graterThan",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
//
//    public static void GE(){
//        String src1 = semanticStack.pop();
//        String src2 = semanticStack.pop();
//        Dscp dscp1 = SymboleTable.find(src1);
//        Dscp dscp2 = SymboleTable.find(src2);
//        String type = getType(dscp1, dscp2, "compare");
//        VariableDscp d = new VariableDscp(type);
//        String dName = "$" + pc;
//        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
//        mipsCode.add(new Code("GraterEqual",dName, src1, src2));
//        pc++;
//        semanticStack.push(dName);
//    }
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
        if (mipsCode.get(Integer.parseInt(jzPC.getValue())).dest == null) {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).dest = "L" + labelNum + ":";
        } else {
            mipsCode.get(Integer.parseInt(jzPC.getValue())).src1 = "L" + labelNum + ":";
        }
        labelNum++;
        Token whilePC = semanticStack.pop();
        mipsCode.add(new Code("b", mipsCode.get(Integer.parseInt(whilePC.getValue())).dest));
        pc++;
        mipsCode.add(new Code("label", "L" + labelNum + ":"));
        labelNum++;
        pc++;
    }
}
