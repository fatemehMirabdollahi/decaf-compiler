package parser;
import scanner.Token;

enum Action{SHIFT, GOTO, PUSH_GOTO, REDUCE, ACCEPT}
public class ParseCell {
    private int state;
    private Action action;
    private String semantic;
    private Token token;

    public ParseCell(int state, int action, String semantic, Token token) {
        this.state = state;
        this.action = Action.values()[action - 1];
        this.semantic = semantic;
        this.token = token;
    }

    public int getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }

    public String getSemantic() {
        return semantic;
    }

    public Token getToken() {
        return token;
    }
}