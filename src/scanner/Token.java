package scanner;

import java.util.Objects;

public class Token {

    public void setValue(String value) {
        this.value = value;
    }

    private  String value;
    private  TokenType type;


    public Token(String value, TokenType type) {

        this.value = value;
        this.type = type;
    }

    public void setType(TokenType type) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(value, token.value) &&
                type == token.type;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
