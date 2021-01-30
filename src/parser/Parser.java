package parser;

import codegen.CodeGen;
import scanner.DecafScanner;
import scanner.Token;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static codegen.SymboleTable.symboleTables;

enum Action {
    ERROR, SHIFT, GOTO, PUSH_GOTO, REDUCE, ACCEPT
}

class LLCell {
    private Action action;
    private int target;
    private String functions;

    public LLCell(Action action, int target, String functions) {
        this.action = action;
        this.target = target;
        this.functions = functions;
    }

    public Action getAction() {
        return action;
    }

    public int getTarget() {
        return target;
    }

    public String getFunction() {
        return functions;
    }
}


public class Parser {
    public static Token token;
    public static String TABLE_DELIMITER = ",";
    private DecafScanner lexical;
    private boolean debugMode;

    private String[] symbols;
    private LLCell[][] parseTable;
    private int startNode;
    private Deque<Integer> parseStack = new ArrayDeque<>();

    private List<String> recoveryState;

    public Parser(DecafScanner lexical, String nptPath, boolean debugMode) {
        this(lexical, nptPath);
        this.debugMode = debugMode;
    }

    public Parser(DecafScanner lexical, String nptPath) {
        this.lexical = lexical;
        this.recoveryState = new ArrayList<>();

        if (!Files.exists(Paths.get(nptPath))) {
            throw new RuntimeException("Parser table not found: " + nptPath);
        }

        try {
            Scanner in = new Scanner(new FileInputStream(nptPath));
            String[] tmpArr = in.nextLine().trim().split(" ");
            int rowSize = Integer.parseInt(tmpArr[0]);
            int colSize = Integer.parseInt(tmpArr[1]);
            startNode = Integer.parseInt(in.nextLine());
            symbols = in.nextLine().trim().split(TABLE_DELIMITER);

            parseTable = new LLCell[rowSize][colSize];
            for (int i = 0; i < rowSize; i++) {
                tmpArr = in.nextLine().trim().split(TABLE_DELIMITER);
                if (tmpArr.length != colSize) {
                    throw new RuntimeException("Invalid .npt file: File contains rows with length" +
                            " bigger than column size.");
                }

                for (int j = 0; j < colSize; j++) {
                    String[] cellParts = tmpArr[j].split(" ");
                    if (cellParts.length != 3) {
                        throw new RuntimeException("Invalid .npt file: Parser cells must have extactly 3 values.");
                    }
                    Action action = Action.values()[Integer.parseInt(cellParts[0])];
                    int target = Integer.parseInt(cellParts[1]);
                    String function;
                    if (cellParts[2].equals("NoSem")) {
                        function = "";
                    } else {
                        function = cellParts[2];
                    }
                    parseTable[i][j] = new LLCell(action, target, function);
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid .npt file.");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to load .npt file.", e);
        }
    }

    public void parse() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int tokenID = nextTokenID();
        int currentNode = startNode;
        boolean accepted = false;
        while (!accepted) {
            String tokenText = symbols[tokenID];
            LLCell cell = parseTable[currentNode][tokenID];
            if (debugMode) {
                System.out.println("Current token: text='" + symbols[tokenID] + "' id=" + tokenID);
                System.out.println("Current node: " + currentNode);
                System.out.println("Current cell of parser table: " +
                        "target-node=" + cell.getTarget() +
                        " action=" + cell.getAction() +
                        " function=" + cell.getFunction());
                System.out.println(String.join("", Collections.nCopies(50, "-")));
            }
            switch (cell.getAction()) {
                case ERROR:
                    updateRecoveryState(currentNode, tokenText);
                    generateError("Unable to parse input.");
                case SHIFT:
                    doSemantics(cell.getFunction());
                    tokenID = nextTokenID();
                    currentNode = cell.getTarget();
                    recoveryState.clear();
                    break;
                case GOTO:
                    updateRecoveryState(currentNode, tokenText);
                    doSemantics(cell.getFunction());
                    currentNode = cell.getTarget();
                    break;
                case PUSH_GOTO:
                    updateRecoveryState(currentNode, tokenText);
                    parseStack.push(currentNode);
                    currentNode = cell.getTarget();
                    break;
                case REDUCE:
                    if (parseStack.size() == 0) {
                        generateError("Unable to Reduce: token=" + tokenText + " node=" + currentNode);
                    }
                    updateRecoveryState(currentNode, tokenText);
                    int graphToken = cell.getTarget();
                    int preNode = parseStack.pop();
                    doSemantics(parseTable[preNode][graphToken].getFunction());
                    currentNode = parseTable[preNode][graphToken].getTarget();
                    break;
                case ACCEPT:
                    accepted = true;
                    break;
            }
        }
    }

    private void generateError(String message) {
        System.out.flush();
        System.out.println("Error happened while parsing ...");
        for (String state : recoveryState) {
            System.out.println(state);
        }
        throw new RuntimeException(message);
    }

    private void updateRecoveryState(int currentNode, String token) {
        List<String> availableTokens = new ArrayList<>();
        LLCell[] cellTokens = parseTable[currentNode];
        for (int i = 0; i < cellTokens.length; i++) {
            if (cellTokens[i].getAction() != Action.ERROR) {
                availableTokens.add(symbols[i]);
            }
        }
        recoveryState.add("At node " + currentNode + ": current token is " + token + " but except: " + availableTokens);
    }

    private int nextTokenID() throws RuntimeException, IOException {
        token = lexical.tokenReader();

        String tokenStr = token.getValue();
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i].equals(tokenStr)) {
                System.out.println(symbols[i]);
                return i;
            }
        }
        throw new RuntimeException("Undefined token: " + tokenStr);
    }

    private void doSemantics(String function) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (debugMode) {
            System.out.println("Execute semantic codes: " + function);
        }
            if (!function.equals("")) {
                Method method = CodeGen.class.getMethod(function, null);
                method.setAccessible(true);
                method.invoke(null, null);
            }

    }
}
