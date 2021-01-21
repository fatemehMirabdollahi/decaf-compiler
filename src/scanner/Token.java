package scanner;

public class Token {

    private  String value;
    private  TokenType type;


    public Token(String value, TokenType type) {

        this.value = value;
        this.type = type;
    }

    public Token(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return value;
    }
}
