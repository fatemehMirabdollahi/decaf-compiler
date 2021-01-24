package codegen;

import codegen.discriptors.Dscp;
import codegen.discriptors.DscpType;
import codegen.discriptors.VariableDscp;
import scanner.Token;
import scanner.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

import static codegen.CodeGen.cast;

public class SymboleTable extends HashMap<String, Dscp> {

    public static ArrayList<SymboleTable> symboleTables;

    public void add(String name, Dscp dscp) {
        if (this.containsKey(name)) {
            //error
        }
        this.put(name, dscp);
    }

    public static Dscp find(Token s) {
        if (s.getType().toString() == "integer") {
            VariableDscp d = new VariableDscp("int",false);
            d.value = s.getValue();
            return d;
        }
        if (s.getType().toString() == "real") {
            VariableDscp d = new VariableDscp("double",false);
            d.value = s.getValue();
            return d;
        }

        for (int i = symboleTables.size() - 1; i >= 0; i--) {
            Dscp d = symboleTables.get(i).get(s);
            if (d != null)
                return d;
        }

        //error
        return null;
    }

    public static boolean checkInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean checkDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getType(Dscp d1, Dscp d2, String op) {

        switch (op) {
            case "Arith":
            case "compare":
                if (d1.dscpType != DscpType.variable || d2.dscpType != DscpType.variable) {
                    //error
                } else {
                    String t1 = ((VariableDscp) d1).type;
                    String t2 = ((VariableDscp) d2).type;
                    if ((t1 == t2) && (t1 == "int" || t1 == "double")) {

                        return op == "Arith" ? t1 : "boolean";

                    } else if (t1 == "int" && t2 == "double") {

                        cast(d1, "double");
                        return op == "Arith" ? "double" : "boolean";

                    } else if (t1 == "double" && t2 == "int") {

                        cast(d2, "double");
                        return op == "Arith" ? "double" : "boolean";

                    } else if (t1 == "boolean" && (t2 == "int" || t2 == "double")) {

                        cast(d1, t2);
                        return op == "Arith" ? t2 : "boolean";

                    } else if (t2 == "boolean" && (t1 == "int" || t1 == "double")) {

                        cast(d2, t1);
                        return op == "Arith" ? t1 : "boolean";

                    } else {
                        //error
                    }
                }
                break;

        }
        return null;
    }

    public static int castBe(Token token) {
        switch (token.getType()) {
            case keyword:
                if (token.getValue() == "true") return 1;
                else return 0;
            case real:

                if (Double.parseDouble(token.getValue()) > 0) return 1;
                else return 0;
            case integer:
                if (Integer.parseInt(token.getValue()) > 0) return 1;
                else return 0;
            case str_char:
                if(token.getValue().length()>2)
                    return 1;
                else return 0;
        }
        return -1;
    }

}
