import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;


public class Highlighter {
    public static void main(String[] args) throws IOException {
        String color = "";
        DecafScanner scanner = new DecafScanner(new FileReader("src/chum.txt"));
        while(true){
            Token token = scanner.tokenReader();
            if(token.getValue().equals("$"))
                break;
            switch (token.getType()){
                case keyword: color = "bold & blue"; break;
                case id: color = "violet"; break;
                case integer: color = "orange"; break;
                case real: color = "italic & orange"; break;
                case str_char: color = "green"; break;
                case spChar: color = "italic & green"; break;
                case comment: color = "yellow"; break;
                case op_punc: color = "black"; break;
                case undefind: color = "red"; break;
                }
            System.out.println(color);
        }
    }
}
