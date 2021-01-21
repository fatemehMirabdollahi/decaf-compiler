package codegen;

import codegen.discriptors.Dscp;
import scanner.Token;

import java.util.HashMap;

public class SymboleTable extends HashMap<String, Dscp> {


    public void add(Token token, Dscp dscp){
        if(this.containsKey(token.getValue())){
            //error
        }
        this.put(token.getValue(), dscp);
    }

}
