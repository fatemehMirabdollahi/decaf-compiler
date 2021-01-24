package codegen.discriptors;

public class VarType {

    public Type type;
    public int size;

    public VarType(Type type) {
        this.type = type;
        switch (type) {
            case Integer:
                size = 4;
                break;
            case Double:
                size = 8;
                break;
            case Boolean:
                size = 4;
                break;
            case String:
                size = 64;
                break;
        }
    }
}
