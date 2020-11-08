// DO NOT EDIT
// Generated by JFlex 1.8.1 http://jflex.de/
// source: Scanner.flex




// See https://github.com/jflex-de/jflex/issues/222
@SuppressWarnings("FallThrough")
public class DecafScanner {

  /** This character denotes the end of file. */
  public static final int YYEOF = -1;

  /** Initial size of the lookahead buffer. */
  private static final int ZZ_BUFFERSIZE = 16384;

  // Lexical states.
  public static final int YYINITIAL = 0;
  public static final int SPECIAL = 2;
  public static final int STRING = 4;
  public static final int CHARACTER = 6;
  public static final int ENDOFCHAR = 8;
  public static final int SPACE = 10;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5, 6
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\25\u0100\1\u0200\11\u0100\1\u0300\17\u0100\1\u0400\u10cf\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\2\1\1\3\22\0\1\1\1\4"+
    "\1\5\2\0\1\6\1\7\1\10\2\6\1\11\1\12"+
    "\1\6\1\13\1\14\1\15\1\16\11\17\1\0\1\6"+
    "\1\4\1\20\1\4\2\0\1\21\3\22\1\23\1\22"+
    "\2\24\1\25\2\24\1\26\1\24\1\27\1\24\1\30"+
    "\1\24\1\31\5\24\1\32\2\24\1\6\1\33\2\6"+
    "\1\34\1\0\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\44\1\45\1\24\1\46\1\47\1\24\1\50"+
    "\1\51\2\24\1\52\1\53\1\54\1\55\1\56\1\57"+
    "\1\32\1\60\1\24\1\0\1\61\10\0\1\1\32\0"+
    "\1\1\u01df\0\1\1\177\0\13\1\35\0\2\1\5\0"+
    "\1\1\57\0\1\1\240\0\1\1\377\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[1280];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\7\0\1\1\1\2\2\3\1\4\1\5\2\4\1\6"+
    "\3\4\2\7\1\1\20\10\1\4\1\11\1\12\1\11"+
    "\2\13\1\14\1\15\4\16\1\0\1\17\1\0\1\20"+
    "\1\21\1\0\13\10\1\22\7\10\1\23\1\24\1\25"+
    "\1\15\1\25\1\0\2\20\1\0\1\7\12\10\1\22"+
    "\5\10\1\26\1\0\1\21\4\10\1\22\11\10\1\22"+
    "\14\10\1\22\5\10\1\22\6\10\1\22\24\10";

  private static int [] zzUnpackAction() {
    int [] result = new int[165];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\62\0\144\0\226\0\310\0\372\0\u012c\0\u015e"+
    "\0\u015e\0\u015e\0\u0190\0\u01c2\0\u015e\0\u015e\0\u01f4\0\u0226"+
    "\0\u0258\0\u028a\0\u02bc\0\u02ee\0\u0320\0\u01c2\0\u0352\0\u0384"+
    "\0\u03b6\0\u03e8\0\u041a\0\u044c\0\u047e\0\u04b0\0\u04e2\0\u0514"+
    "\0\u0546\0\u0578\0\u05aa\0\u05dc\0\u060e\0\u0640\0\u0672\0\u015e"+
    "\0\u015e\0\u06a4\0\u015e\0\u06d6\0\u015e\0\u0708\0\u015e\0\u073a"+
    "\0\u076c\0\u079e\0\u0226\0\u015e\0\u07d0\0\u0802\0\u0834\0\u0866"+
    "\0\u0898\0\u08ca\0\u08fc\0\u092e\0\u0960\0\u0992\0\u09c4\0\u09f6"+
    "\0\u0a28\0\u0a5a\0\u0a8c\0\u0898\0\u0abe\0\u0af0\0\u0b22\0\u0b54"+
    "\0\u0b86\0\u0bb8\0\u0bea\0\u015e\0\u015e\0\u015e\0\u076c\0\u076c"+
    "\0\u0c1c\0\u015e\0\u0c4e\0\u0c80\0\u0866\0\u0cb2\0\u0ce4\0\u0d16"+
    "\0\u0d48\0\u0d7a\0\u0dac\0\u0dde\0\u0e10\0\u0e42\0\u0e74\0\u0cb2"+
    "\0\u0ea6\0\u0ed8\0\u0f0a\0\u0f3c\0\u0f6e\0\u015e\0\u0fa0\0\u0fa0"+
    "\0\u0fd2\0\u1004\0\u1036\0\u1068\0\u0fd2\0\u109a\0\u10cc\0\u10fe"+
    "\0\u1130\0\u1162\0\u1194\0\u11c6\0\u11f8\0\u122a\0\u11f8\0\u125c"+
    "\0\u128e\0\u12c0\0\u12f2\0\u1324\0\u1356\0\u1388\0\u13ba\0\u13ec"+
    "\0\u141e\0\u1450\0\u1482\0\u13ba\0\u14b4\0\u14e6\0\u1518\0\u154a"+
    "\0\u157c\0\u157c\0\u15ae\0\u15e0\0\u1612\0\u1644\0\u1676\0\u16a8"+
    "\0\u16a8\0\u16da\0\u170c\0\u173e\0\u1770\0\u17a2\0\u17d4\0\u1806"+
    "\0\u1838\0\u186a\0\u189c\0\u18ce\0\u1900\0\u1932\0\u1964\0\u1996"+
    "\0\u19c8\0\u19fa\0\u1a2c\0\u1a5e\0\u015e";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[165];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17"+
    "\1\20\1\14\1\21\1\22\1\16\1\23\1\24\1\25"+
    "\1\26\6\27\1\30\1\31\1\32\1\27\2\10\1\27"+
    "\1\33\1\34\1\35\1\36\1\37\2\27\1\40\2\27"+
    "\1\41\1\27\1\42\1\43\1\44\1\27\1\45\1\46"+
    "\1\27\1\47\1\10\1\11\1\12\1\13\56\10\2\50"+
    "\1\12\1\13\1\50\1\51\25\50\1\52\26\50\2\53"+
    "\1\12\1\13\27\53\1\54\26\53\1\10\1\11\1\12"+
    "\1\13\4\10\1\55\51\10\1\56\2\57\1\60\57\56"+
    "\1\57\1\61\1\62\56\56\64\0\1\12\77\0\1\16"+
    "\50\0\1\16\52\0\10\63\1\64\51\63\12\0\1\16"+
    "\5\0\1\16\54\0\1\16\4\0\1\16\52\0\1\65"+
    "\3\0\1\66\2\0\1\16\55\0\1\67\1\0\2\25"+
    "\12\0\1\70\43\0\1\67\1\0\2\25\60\0\2\71"+
    "\1\0\12\71\1\0\25\71\17\0\2\71\1\0\12\71"+
    "\1\0\5\71\1\72\17\71\17\0\2\71\1\0\12\71"+
    "\1\0\16\71\1\73\6\71\17\0\2\71\1\0\12\71"+
    "\1\0\5\71\1\74\17\71\17\0\2\71\1\0\12\71"+
    "\1\0\15\71\1\75\1\76\6\71\17\0\2\71\1\0"+
    "\12\71\1\0\15\71\1\77\7\71\17\0\2\71\1\0"+
    "\12\71\1\0\15\71\1\100\7\71\17\0\2\71\1\0"+
    "\12\71\1\0\13\71\1\101\11\71\17\0\2\71\1\0"+
    "\12\71\1\0\1\71\1\102\13\71\1\103\7\71\17\0"+
    "\2\71\1\0\12\71\1\0\6\71\1\104\5\71\1\105"+
    "\10\71\17\0\2\71\1\0\12\71\1\0\5\71\1\106"+
    "\17\71\17\0\2\71\1\0\12\71\1\0\5\71\1\107"+
    "\17\71\17\0\2\71\1\0\12\71\1\0\20\71\1\110"+
    "\4\71\17\0\2\71\1\0\12\71\1\0\16\71\1\111"+
    "\6\71\17\0\2\71\1\0\12\71\1\0\15\71\1\112"+
    "\7\71\17\0\2\71\1\0\12\71\1\0\10\71\1\113"+
    "\14\71\62\0\1\16\36\0\1\114\1\0\1\114\7\0"+
    "\1\114\1\0\1\114\1\0\1\114\43\0\1\115\1\0"+
    "\1\115\7\0\1\115\1\0\1\115\1\0\1\115\5\0"+
    "\1\56\3\0\56\56\2\0\1\116\61\0\2\117\60\0"+
    "\1\120\1\117\56\0\11\65\1\121\50\65\2\66\1\122"+
    "\1\123\56\66\16\0\2\67\3\0\1\124\54\0\2\125"+
    "\1\0\3\125\11\0\6\125\35\0\2\126\1\0\12\126"+
    "\1\0\25\126\17\0\2\126\1\0\12\126\1\0\23\126"+
    "\1\127\1\126\17\0\2\126\1\0\12\126\1\0\11\126"+
    "\1\130\13\126\17\0\2\126\1\0\12\126\1\0\1\126"+
    "\1\131\23\126\17\0\2\126\1\0\12\126\1\0\15\126"+
    "\1\132\7\126\17\0\2\126\1\0\12\126\1\0\5\126"+
    "\1\133\17\126\17\0\2\126\1\0\12\126\1\0\14\126"+
    "\1\134\10\126\17\0\2\126\1\0\12\126\1\0\21\126"+
    "\1\135\3\126\17\0\2\126\1\0\12\126\1\0\17\126"+
    "\1\136\5\126\17\0\2\126\1\0\12\126\1\0\13\126"+
    "\1\137\11\126\17\0\2\126\1\0\12\126\1\0\16\126"+
    "\1\140\6\126\17\0\2\126\1\0\12\126\1\0\20\126"+
    "\1\140\4\126\17\0\2\126\1\0\12\126\1\0\23\126"+
    "\1\140\1\126\17\0\2\126\1\0\12\126\1\0\3\126"+
    "\1\141\14\126\1\142\4\126\17\0\2\126\1\0\12\126"+
    "\1\0\16\126\1\143\6\126\17\0\2\126\1\0\12\126"+
    "\1\0\21\126\1\136\3\126\17\0\2\126\1\0\12\126"+
    "\1\0\11\126\1\144\13\126\17\0\2\126\1\0\12\126"+
    "\1\0\11\126\1\145\13\126\1\0\11\65\1\121\3\65"+
    "\1\146\44\65\2\0\1\122\71\0\2\147\2\0\2\150"+
    "\60\0\2\151\1\0\12\151\1\0\25\151\17\0\2\151"+
    "\1\0\1\152\11\151\1\0\25\151\17\0\2\151\1\0"+
    "\12\151\1\0\14\151\1\153\10\151\17\0\2\151\1\0"+
    "\12\151\1\0\4\151\1\154\20\151\17\0\2\151\1\0"+
    "\12\151\1\0\13\151\1\155\11\151\17\0\2\151\1\0"+
    "\12\151\1\0\1\151\1\156\23\151\17\0\2\151\1\0"+
    "\12\151\1\0\20\151\1\157\4\151\17\0\2\151\1\0"+
    "\12\151\1\0\2\151\1\160\22\151\17\0\2\151\1\0"+
    "\12\151\1\0\5\151\1\155\17\151\17\0\2\151\1\0"+
    "\12\151\1\0\17\151\1\161\5\151\17\0\2\151\1\0"+
    "\12\151\1\0\15\151\1\162\7\151\17\0\2\151\1\0"+
    "\12\151\1\0\21\151\1\163\3\151\17\0\2\151\1\0"+
    "\12\151\1\0\11\151\1\164\13\151\17\0\2\151\1\0"+
    "\12\151\1\0\4\151\1\155\20\151\17\0\2\151\1\0"+
    "\12\151\1\0\13\151\1\161\11\151\17\0\2\150\60\0"+
    "\2\165\1\0\12\165\1\0\25\165\17\0\2\165\1\0"+
    "\12\165\1\0\16\165\1\166\6\165\17\0\2\165\1\0"+
    "\12\165\1\0\20\165\1\167\4\165\17\0\2\165\1\0"+
    "\4\165\1\170\1\171\4\165\1\0\25\165\17\0\2\165"+
    "\1\0\12\165\1\0\12\165\1\167\12\165\17\0\2\165"+
    "\1\0\12\165\1\0\11\165\1\172\13\165\17\0\2\165"+
    "\1\0\12\165\1\0\13\165\1\173\11\165\17\0\2\165"+
    "\1\0\12\165\1\0\5\165\1\167\17\165\17\0\2\165"+
    "\1\0\12\165\1\0\16\165\1\174\6\165\17\0\2\165"+
    "\1\0\12\165\1\0\16\165\1\175\6\165\17\0\2\165"+
    "\1\0\12\165\1\0\14\165\1\176\10\165\17\0\2\177"+
    "\1\0\12\177\1\0\25\177\17\0\2\177\1\0\12\177"+
    "\1\0\16\177\1\200\6\177\17\0\2\177\1\0\12\177"+
    "\1\0\14\177\1\201\10\177\17\0\2\177\1\0\12\177"+
    "\1\0\11\177\1\202\13\177\17\0\2\177\1\0\12\177"+
    "\1\0\14\177\1\203\10\177\17\0\2\177\1\0\12\177"+
    "\1\0\5\177\1\204\17\177\17\0\2\177\1\0\12\177"+
    "\1\0\4\177\1\204\20\177\17\0\2\177\1\0\12\177"+
    "\1\0\14\177\1\204\10\177\17\0\2\177\1\0\12\177"+
    "\1\0\7\177\1\204\15\177\17\0\2\205\1\0\12\205"+
    "\1\0\25\205\17\0\2\205\1\0\12\205\1\0\1\205"+
    "\1\206\23\205\17\0\2\205\1\0\12\205\1\0\20\205"+
    "\1\207\4\205\17\0\2\205\1\0\12\205\1\0\14\205"+
    "\1\210\10\205\17\0\2\205\1\0\12\205\1\0\21\205"+
    "\1\210\3\205\17\0\2\211\1\0\12\211\1\0\25\211"+
    "\17\0\2\211\1\0\12\211\1\0\24\211\1\212\17\0"+
    "\2\211\1\0\12\211\1\0\5\211\1\213\17\211\17\0"+
    "\2\211\1\0\12\211\1\0\5\211\1\212\17\211\17\0"+
    "\2\214\1\0\12\214\1\0\25\214\17\0\2\214\1\0"+
    "\12\214\1\0\7\214\1\215\15\214\17\0\2\216\1\0"+
    "\12\216\1\0\25\216\17\0\2\216\1\0\12\216\1\0"+
    "\5\216\1\217\17\216\17\0\2\220\1\0\12\220\1\0"+
    "\25\220\17\0\2\220\1\0\12\220\1\0\16\220\1\221"+
    "\6\220\17\0\2\222\1\0\12\222\1\0\25\222\17\0"+
    "\2\223\1\0\12\223\1\0\25\223\17\0\2\224\1\0"+
    "\12\224\1\0\25\224\17\0\2\225\1\0\12\225\1\0"+
    "\25\225\17\0\2\226\1\0\12\226\1\0\25\226\17\0"+
    "\2\227\1\0\12\227\1\0\25\227\17\0\2\230\1\0"+
    "\12\230\1\0\25\230\17\0\2\231\1\0\12\231\1\0"+
    "\25\231\17\0\2\232\1\0\12\232\1\0\25\232\17\0"+
    "\2\233\1\0\12\233\1\0\25\233\17\0\2\234\1\0"+
    "\12\234\1\0\25\234\17\0\2\235\1\0\12\235\1\0"+
    "\25\235\17\0\2\236\1\0\12\236\1\0\25\236\17\0"+
    "\2\237\1\0\12\237\1\0\25\237\17\0\2\240\1\0"+
    "\12\240\1\0\25\240\17\0\2\241\1\0\12\241\1\0"+
    "\25\241\17\0\2\242\1\0\12\242\1\0\25\242\17\0"+
    "\2\243\1\0\12\243\1\0\25\243\17\0\2\244\1\0"+
    "\12\244\1\0\25\244\17\0\2\245\1\0\12\245\1\0"+
    "\25\245\1\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[6800];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** Error code for "Unknown internal scanner error". */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  /** Error code for "could not match input". */
  private static final int ZZ_NO_MATCH = 1;
  /** Error code for "pushback value was too large". */
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /**
   * Error messages for {@link #ZZ_UNKNOWN_ERROR}, {@link #ZZ_NO_MATCH}, and
   * {@link #ZZ_PUSHBACK_2BIG} respectively.
   */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\7\0\3\11\2\1\2\11\31\1\2\11\1\1\1\11"+
    "\1\1\1\11\1\1\1\11\3\1\1\0\1\11\1\0"+
    "\2\1\1\0\23\1\3\11\2\1\1\0\1\11\1\1"+
    "\1\0\21\1\1\11\1\0\75\1\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[165];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** Input device. */
  private java.io.Reader zzReader;

  /** Current state of the DFA. */
  private int zzState;

  /** Current lexical state. */
  private int zzLexicalState = YYINITIAL;

  /**
   * This buffer contains the current text to be matched and is the source of the {@link #yytext()}
   * string.
   */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** Text position at the last accepting state. */
  private int zzMarkedPos;

  /** Current text position in the buffer. */
  private int zzCurrentPos;

  /** Marks the beginning of the {@link #yytext()} string in the buffer. */
  private int zzStartRead;

  /** Marks the last character in the buffer, that has been read from input. */
  private int zzEndRead;

  /**
   * Whether the scanner is at the end of file.
   * @see #yyatEOF
   */
  private boolean zzAtEOF;

  /**
   * The number of occupied positions in {@link #zzBuffer} beyond {@link #zzEndRead}.
   *
   * <p>When a lead/high surrogate has been read from the input stream into the final
   * {@link #zzBuffer} position, this will have a value of 1; otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /** Number of newlines encountered up to the start of the matched text. */
  @SuppressWarnings("unused")
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  @SuppressWarnings("unused")
  private int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  @SuppressWarnings("unused")
  private boolean zzEOFDone;

  /* user code: */
String myString = "";
String myCharacter = "";
Token token ;
boolean endl =false;


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public DecafScanner(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  /**
   * Refills the input buffer.
   *
   * @return {@code false} iff there was new input.
   * @exception java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead - zzStartRead);

      /* translate stored positions */
      zzEndRead -= zzStartRead;
      zzCurrentPos -= zzStartRead;
      zzMarkedPos -= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length * 2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException(
          "Reader returned 0 characters. See JFlex examples/zero-reader for a workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
        if (numRead == requested) { // We requested too few chars to encode a full Unicode character
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        } else {                    // There is room in the buffer for at least one more char
          int c = zzReader.read();  // Expecting to read a paired low surrogate char
          if (c == -1) {
            return true;
          } else {
            zzBuffer[zzEndRead++] = (char)c;
          }
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }


  /**
   * Closes the input reader.
   *
   * @throws java.io.IOException if the reader could not be closed.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true; // indicate end of file
    zzEndRead = zzStartRead; // invalidate buffer

    if (zzReader != null) {
      zzReader.close();
    }
  }


  /**
   * Resets the scanner to read from a new input stream.
   *
   * <p>Does not close the old reader.
   *
   * <p>All internal variables are reset, the old input stream <b>cannot</b> be reused (internal
   * buffer is discarded and lost). Lexical state is set to {@code ZZ_INITIAL}.
   *
   * <p>Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader The new input stream.
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzEOFDone = false;
    yyResetPosition();
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE) {
      zzBuffer = new char[ZZ_BUFFERSIZE];
    }
  }

  /**
   * Resets the input position.
   */
  private final void yyResetPosition() {
      zzAtBOL  = true;
      zzAtEOF  = false;
      zzCurrentPos = 0;
      zzMarkedPos = 0;
      zzStartRead = 0;
      zzEndRead = 0;
      zzFinalHighSurrogate = 0;
      yyline = 0;
      yycolumn = 0;
      yychar = 0L;
  }


  /**
   * Returns whether the scanner has reached the end of the reader it reads from.
   *
   * @return whether the scanner has reached EOF.
   */
  public final boolean yyatEOF() {
    return zzAtEOF;
  }


  /**
   * Returns the current lexical state.
   *
   * @return the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state.
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   *
   * @return the matched text.
   */
  public final String yytext() {
    return new String(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
  }


  /**
   * Returns the character at the given position from the matched text.
   *
   * <p>It is equivalent to {@code yytext().charAt(pos)}, but faster.
   *
   * @param position the position of the character to fetch. A value from 0 to {@code yylength()-1}.
   *
   * @return the character at {@code position}.
   */
  public final char yycharat(int position) {
    return zzBuffer[zzStartRead + position];
  }


  /**
   * How many characters were matched.
   *
   * @return the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * <p>In a well-formed scanner (no or only correct usage of {@code yypushback(int)} and a
   * match-all fallback rule) this method will only be called with things that
   * "Can't Possibly Happen".
   *
   * <p>If this method is called, something is seriously wrong (e.g. a JFlex bug producing a faulty
   * scanner etc.).
   *
   * <p>Usual syntax/scanner level error handling should be done in error fallback rules.
   *
   * @param errorCode the code of the error message to display.
   */
  private static void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    } catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * <p>They will be read again by then next call of the scanning method.
   *
   * @param number the number of characters to be read again. This number must not be greater than
   *     {@link #yylength()}.
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }




  /**
   * Resumes scanning until the next regular expression is matched, the end of input is encountered
   * or an I/O-Error occurs.
   *
   * @return the next token.
   * @exception java.io.IOException if any I/O-Error occurs.
   */
  public Token tokenReader() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char[] zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      if (zzMarkedPosL > zzStartRead) {
        switch (zzBufferL[zzMarkedPosL-1]) {
        case '\n':
        case '\u000B':  // fall through
        case '\u000C':  // fall through
        case '\u0085':  // fall through
        case '\u2028':  // fall through
        case '\u2029':  // fall through
          zzAtBOL = true;
          break;
        case '\r': 
          if (zzMarkedPosL < zzEndReadL)
            zzAtBOL = zzBufferL[zzMarkedPosL] != '\n';
          else if (zzAtEOF)
            zzAtBOL = false;
          else {
            boolean eof = zzRefill();
            zzMarkedPosL = zzMarkedPos;
            zzEndReadL = zzEndRead;
            zzBufferL = zzBuffer;
            if (eof) 
              zzAtBOL = false;
            else 
              zzAtBOL = zzBufferL[zzMarkedPosL] != '\n';
          }
          break;
        default:
          zzAtBOL = false;
        }
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      if (zzAtBOL)
        zzState = ZZ_LEXSTATE[zzLexicalState+1];
      else
        zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            switch (zzLexicalState) {
            case SPACE: {
              yybegin(YYINITIAL); return token;
            }  // fall though
            case 166: break;
            default:
              {
                return new Token("$");
              }
        }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);
            }
            // fall through
          case 23: break;
          case 2:
            { yybegin(YYINITIAL);/*return new Token(yytext(),Type.undefind);*/
            }
            // fall through
          case 24: break;
          case 3:
            { yybegin(YYINITIAL); endl=true;/*return new Token(yytext(),Type.undefind);*/
            }
            // fall through
          case 25: break;
          case 4:
            { yybegin(SPACE);token = new Token(yytext(),Type.op_punc);
            }
            // fall through
          case 26: break;
          case 5:
            { yybegin(STRING);myString="\"";
            }
            // fall through
          case 27: break;
          case 6:
            { yybegin(CHARACTER);myCharacter="\'";
            }
            // fall through
          case 28: break;
          case 7:
            { yybegin(SPACE);token = new Token(yytext(),Type.integer);
            }
            // fall through
          case 29: break;
          case 8:
            { yybegin(SPACE);token = new Token(yytext(),Type.id);
            }
            // fall through
          case 30: break;
          case 9:
            { myString+=yytext();
            }
            // fall through
          case 31: break;
          case 10:
            { yybegin(SPACE); myString+="\""  ;token = new Token(myString,Type.str_char);
            }
            // fall through
          case 32: break;
          case 11:
            { myCharacter+=yytext();yybegin(ENDOFCHAR);
            }
            // fall through
          case 33: break;
          case 12:
            { yybegin(SPACE);myCharacter +="\'";  token = new Token(myCharacter,Type.str_char);
            }
            // fall through
          case 34: break;
          case 13:
            { yybegin(YYINITIAL);System.out.println("hii");return new Token(token.getValue()+yytext(),Type.undefind);
            }
            // fall through
          case 35: break;
          case 14:
            { yybegin(YYINITIAL); return token;
            }
            // fall through
          case 36: break;
          case 15:
            { return new Token(yytext(),Type.undefind);
            }
            // fall through
          case 37: break;
          case 16:
            { yybegin(YYINITIAL); endl=true;return new Token(yytext(),Type.comment);
            }
            // fall through
          case 38: break;
          case 17:
            { yybegin(SPACE);token = new Token(yytext(),Type.real);
            }
            // fall through
          case 39: break;
          case 18:
            { yybegin(SPACE);token = new Token(yytext(),Type.keyword);
            }
            // fall through
          case 40: break;
          case 19:
            { myString+="<i>"+yytext()+"</i>";
            }
            // fall through
          case 41: break;
          case 20:
            { myCharacter+="<i>"+yytext()+"</i>";yybegin(ENDOFCHAR);
            }
            // fall through
          case 42: break;
          case 21:
            { yybegin(YYINITIAL); endl=true;return token;
            }
            // fall through
          case 43: break;
          case 22:
            { yybegin(SPACE);token = new Token(yytext(),Type.comment);
            }
            // fall through
          case 44: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }

  /**
   * Runs the scanner on input files.
   *
   * This is a standalone scanner, it will print any unmatched
   * text to System.out unchanged.
   *
   * @param argv   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String argv[]) {
    if (argv.length == 0) {
      System.out.println("Usage : java DecafScanner [ --encoding <name> ] <inputfile(s)>");
    }
    else {
      int firstFilePos = 0;
      String encodingName = "UTF-8";
      if (argv[0].equals("--encoding")) {
        firstFilePos = 2;
        encodingName = argv[1];
        try {
          // Side-effect: is encodingName valid?
          java.nio.charset.Charset.forName(encodingName);
        } catch (Exception e) {
          System.out.println("Invalid encoding '" + encodingName + "'");
          return;
        }
      }
      for (int i = firstFilePos; i < argv.length; i++) {
        DecafScanner scanner = null;
        try {
          java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
          java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
          scanner = new DecafScanner(reader);
          while ( !scanner.zzAtEOF ) scanner.tokenReader();
        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+argv[i]+"\"");
        }
        catch (java.io.IOException e) {
          System.out.println("IO error scanning file \""+argv[i]+"\"");
          System.out.println(e);
        }
        catch (Exception e) {
          System.out.println("Unexpected exception:");
          e.printStackTrace();
        }
      }
    }
  }


}
