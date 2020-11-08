enum Type{keyword, id, integer, real, str_char, spChar, comment, op_punc, undefind, end}
public class Token {

    private  String value;
    private  Type type;
    private  String color;


    public Token(String value, Type type) {
        this.value = value;
        this.type = type;
        setAttrColor();
    }

    public void setAttrColor() {

        switch (this.type) {
            case keyword:
                this.color = "#0099ff";
                this.value = "<b>" + this.value + "</b>";
                break;
            case id:
                this.color = "#a148db";
                break;
            case integer:
                this.color = "#ffa200";
                break;
            case real:
                this.value = "<i>" + this.value + "</i>";
                this.color = "#ffa200";
                break;
            case str_char:
                this.color = "#61ff73";
                break;
            case spChar:
                this.color = "#61ff73";
                break;
            case comment:
                this.color = "#fff300";
                break;
            case op_punc:
                this.color = "#000000";
                break;
            case undefind:
                this.color = "#ff0000";
                break;
        }

    }
    public Token(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return value;
    }
}
