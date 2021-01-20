package scanner;

enum Type{keyword, id, integer, real, str_char, spChar, comment, op_punc, undefined, end}
public class Token {

    private  String value;
    private  Type type;


    public Token(String value, Type type) {

        this.value = value;
        this.type = type;
    }

    public Token(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return value;
    }
}
