package codegen;

import codegen.discriptors.Dscp;
import codegen.discriptors.DscpType;
import codegen.discriptors.VariableDscp;
import scanner.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class SymboleTable extends HashMap<String, Dscp> {

    public static ArrayList<SymboleTable> symboleTables;

    public void add(String name, Dscp dscp) {
        if (this.containsKey(name)) {
            //error
        }
        this.put(name, dscp);
    }

    public static Dscp find(String s) {
        if(checkInt(s)){
            VariableDscp d = new VariableDscp("int");
            d.value = s;
            return d;
        }
        if(checkDouble(s)){
            VariableDscp d = new VariableDscp("double");
            d.value = s;
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

    public static String getType(Dscp d1, Dscp d2, String op){

        switch (op){
            case "Arith":
                if(d1.dscpType!= DscpType.variable || d2.dscpType!= DscpType.variable){
                    //error
                } else {
                    String t1 = ((VariableDscp) d1).type;
                    String t2 = ((VariableDscp) d2).type;
                    if(t1 == "int" && t1 == "int"){
                        return "int";
                    } else if(t1 == "int" && t2 =="double" ||
                              t1 == "double" && t2 =="int" ||
                              t1 == "double" && t2 =="double") {
                        return "double";
                    } else{
                        //error
                    }
                }





        }
        return null;
    }

}
