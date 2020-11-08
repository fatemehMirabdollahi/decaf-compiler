import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Highlighter {
    static Document doc ;
    static DecafScanner scanner;

    public static void WriteHtml(Token token) throws IOException {
        Element element = doc.select("body").first();
        if(scanner.endl && token.getType().toString()!="comment")
        {
            element.append("<br>");
            scanner.endl=false;
        }
        if(token.getType().toString()=="comment")
            element.append("<pre style= \"color:"+ token.getColor() + "\">"+token + "</pre>");
        else
            element.append("<span style= \"color:"+ token.getColor() + "\">"+token+" " + "</span>");

    }

    public static void main(String[] args) throws IOException {
        File input = new File("./src/ans.html");
        doc = Jsoup.parse(input, "UTF-8", "");
        scanner = new DecafScanner(new FileReader("src/chum.txt"));
        FileWriter fileWriter = new FileWriter(input);
        while (true) {
            Token token = scanner.tokenReader();
            if (token.getValue().equals("$")) {
                fileWriter.write(doc.html());
                fileWriter.flush();
                break;
            }
            WriteHtml( token);
            System.out.println(token.getType());
        }
    }
}
