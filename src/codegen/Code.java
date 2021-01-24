package codegen;

public class Code {
    public String opcode ; //enum
    public String dest = null;
    public String src1 = null;
    public String src2 = null;

    public Code(String opcode, String dest, String src1, String src2) {
        this.opcode = opcode;
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
    }

    public Code(String opcode, String dest, String src1) {
        this.opcode = opcode;
        this.dest = dest;
        this.src1 = src1;
    }

    public Code(String opcode, String dest) {
        this.opcode = opcode;
        this.dest = dest;
    }

    public Code(String opcode) {
        this.opcode = opcode;
    }
}
