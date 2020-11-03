import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;


public class Highlighter {
    public static void main(String[] args) throws IOException {
        String color;
        DecafScanner scanner = new DecafScanner(new FileReader("chum"));
        while(true){
            Token token = scanner.tokenReader();
            switch (token.getType()){
                case keyword: color = "bold & blue"; /*reserved word*/ break;
                case id: color = "violet"; /*identifiers*/ break;
                case integer: color = "orange"; /*integer*/ break;
                case real: color = "italic & orange"; /*real*/ break;
                case str_char: color = "green"; /*string & char*/ break;
                case spChar: color = "italic & green"; /*special char*/ break;
                case comment: color = "yellow"; /*comment*/ break;
                case op_punc: color = "black"; /*operators and punctuation*/ break;
                case undefind: color = "red"; /*undifiend*/ break;
            }
        }
    }
}
