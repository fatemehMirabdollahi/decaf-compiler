package codegen;

import codegen.discriptors.Dscp;
import codegen.discriptors.VariableDscp;

import static codegen.SymboleTable.getType;
import static codegen.SymboleTable.symboleTables;
import static parser.Parser.*;

public class CodeGen {
    public static void PUSH(){
        semanticStack.push(token.getValue());
    }

    public static void cast(Dscp dscp, String type){}

    public static void ADD(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "Arith");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("add",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void SUB(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "Arith");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("sub",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void MUL(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "Arith");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("mul",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void DIV(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "Arith");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("div",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void LT(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("lessThan",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void LE(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("lessEqual",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void GT(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("graterThan",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }

    public static void GE(){
        String src1 = semanticStack.pop();
        String src2 = semanticStack.pop();
        Dscp dscp1 = SymboleTable.find(src1);
        Dscp dscp2 = SymboleTable.find(src2);
        String type = getType(dscp1, dscp2, "compare");
        VariableDscp d = new VariableDscp(type);
        String dName = "$" + pc;
        symboleTables.get(symboleTables.size() - 1).add(dName, d); //pc ast
        mipsCode.add(new Code("GraterEqual",dName, src1, src2));
        pc++;
        semanticStack.push(dName);
    }


}
