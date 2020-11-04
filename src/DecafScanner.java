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

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1,  1,  2,  2,  3,  3,  4, 4
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\u10ff\u0100";

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
    "\12\0\1\1\2\0\1\2\23\0\1\3\1\4\2\0"+
    "\1\5\1\6\1\7\2\5\1\10\1\11\1\5\1\12"+
    "\1\13\1\14\1\15\11\16\1\0\1\5\1\3\1\17"+
    "\1\3\2\0\4\20\1\21\1\20\21\22\1\23\2\22"+
    "\1\5\1\24\2\5\1\25\1\0\1\20\1\26\1\20"+
    "\1\26\1\27\1\20\7\22\1\30\3\22\1\30\1\22"+
    "\1\30\3\22\1\23\2\22\1\0\1\31\u0183\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[512];
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
    "\5\0\1\1\2\2\1\3\1\4\2\3\1\5\3\3"+
    "\2\6\1\1\1\7\1\1\1\3\2\10\1\11\1\10"+
    "\3\12\1\13\1\14\2\0\1\15\1\16\1\0\1\7"+
    "\1\17\1\20\1\21\2\0\1\6\1\7\1\15\1\0"+
    "\1\16\34\7";

  private static int [] zzUnpackAction() {
    int [] result = new int[75];
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
    "\0\0\0\32\0\64\0\116\0\150\0\202\0\202\0\234"+
    "\0\266\0\202\0\202\0\320\0\352\0\u0104\0\u011e\0\u0138"+
    "\0\u0152\0\u016c\0\266\0\u0186\0\u01a0\0\u01ba\0\202\0\234"+
    "\0\202\0\u01d4\0\202\0\234\0\u01ee\0\u0208\0\u0208\0\352"+
    "\0\u0222\0\u023c\0\u0256\0\u0270\0\u028a\0\202\0\202\0\202"+
    "\0\u02a4\0\u02be\0\u0270\0\u02d8\0\202\0\u02f2\0\u02f2\0\u030c"+
    "\0\u0326\0\u0340\0\u035a\0\u0374\0\u038e\0\u03a8\0\u03c2\0\u03dc"+
    "\0\u03f6\0\u0410\0\u042a\0\u0444\0\u045e\0\u0478\0\u0492\0\u04ac"+
    "\0\u04c6\0\u04e0\0\u04fa\0\u0514\0\u052e\0\u0548\0\u0562\0\u057c"+
    "\0\u0596\0\u05b0\0\202";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[75];
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
    "\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
    "\1\11\1\16\1\17\1\13\1\20\1\21\1\22\1\23"+
    "\4\24\1\25\1\6\3\24\1\26\1\6\1\7\1\10"+
    "\27\6\2\27\1\30\1\27\1\31\17\27\1\32\2\27"+
    "\2\6\1\27\2\33\1\34\21\33\1\35\2\33\2\6"+
    "\1\33\7\36\1\37\22\36\33\0\1\7\47\0\1\13"+
    "\20\0\1\13\23\0\7\40\1\6\22\40\11\0\1\13"+
    "\5\0\1\13\24\0\1\13\4\0\1\13\22\0\1\41"+
    "\3\0\1\42\2\0\1\13\25\0\1\43\1\0\2\22"+
    "\4\0\1\44\21\0\1\43\1\0\2\22\30\0\2\45"+
    "\1\0\4\45\1\0\4\45\27\0\1\46\1\0\1\46"+
    "\32\0\1\13\26\0\1\47\1\0\1\47\27\0\1\50"+
    "\1\0\1\50\1\0\32\36\10\41\1\51\21\41\27\42"+
    "\2\0\1\42\15\0\2\43\2\0\1\52\25\0\2\53"+
    "\1\0\2\53\4\0\2\53\17\0\2\54\1\0\4\54"+
    "\1\0\4\54\1\0\10\41\1\51\3\41\1\55\15\41"+
    "\11\0\2\56\2\0\2\57\30\0\2\60\1\0\4\60"+
    "\1\0\4\60\16\0\2\57\30\0\2\61\1\0\4\61"+
    "\1\0\4\61\16\0\2\62\1\0\4\62\1\0\4\62"+
    "\16\0\2\63\1\0\4\63\1\0\4\63\16\0\2\64"+
    "\1\0\4\64\1\0\4\64\16\0\2\65\1\0\4\65"+
    "\1\0\4\65\16\0\2\66\1\0\4\66\1\0\4\66"+
    "\16\0\2\67\1\0\4\67\1\0\4\67\16\0\2\70"+
    "\1\0\4\70\1\0\4\70\16\0\2\71\1\0\4\71"+
    "\1\0\4\71\16\0\2\72\1\0\4\72\1\0\4\72"+
    "\16\0\2\73\1\0\4\73\1\0\4\73\16\0\2\74"+
    "\1\0\4\74\1\0\4\74\16\0\2\75\1\0\4\75"+
    "\1\0\4\75\16\0\2\76\1\0\4\76\1\0\4\76"+
    "\16\0\2\77\1\0\4\77\1\0\4\77\16\0\2\100"+
    "\1\0\4\100\1\0\4\100\16\0\2\101\1\0\4\101"+
    "\1\0\4\101\16\0\2\102\1\0\4\102\1\0\4\102"+
    "\16\0\2\103\1\0\4\103\1\0\4\103\16\0\2\104"+
    "\1\0\4\104\1\0\4\104\16\0\2\105\1\0\4\105"+
    "\1\0\4\105\16\0\2\106\1\0\4\106\1\0\4\106"+
    "\16\0\2\107\1\0\4\107\1\0\4\107\16\0\2\110"+
    "\1\0\4\110\1\0\4\110\16\0\2\111\1\0\4\111"+
    "\1\0\4\111\16\0\2\112\1\0\4\112\1\0\4\112"+
    "\16\0\2\113\1\0\4\113\1\0\4\113\1\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1482];
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
    "\5\0\2\11\2\1\2\11\13\1\1\11\1\1\1\11"+
    "\1\1\1\11\4\1\2\0\2\1\1\0\1\1\3\11"+
    "\2\0\2\1\1\11\1\0\34\1\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[75];
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
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  @SuppressWarnings("unused")
  private boolean zzEOFDone;

  /* user code: */
String myString = "";
String mySpecial ="";
String myCharacter = "";


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

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

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
              {
                return new Token("$");
              }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);
            }
            // fall through
          case 18: break;
          case 2:
            { yybegin(YYINITIAL);/*return new Token(yytext(),Type.undefind);*/
            }
            // fall through
          case 19: break;
          case 3:
            { yybegin(YYINITIAL);return new Token(yytext(),Type.op_punc);
            }
            // fall through
          case 20: break;
          case 4:
            { yybegin(STRING);myString="\"";
            }
            // fall through
          case 21: break;
          case 5:
            { yybegin(CHARACTER);myCharacter="\'";
            }
            // fall through
          case 22: break;
          case 6:
            { yybegin(YYINITIAL);return new Token(yytext(),Type.integer);
            }
            // fall through
          case 23: break;
          case 7:
            { return new Token(yytext(),Type.id);
            }
            // fall through
          case 24: break;
          case 8:
            { myString+=yytext();
            }
            // fall through
          case 25: break;
          case 9:
            { yybegin(YYINITIAL); myString+="\""  ;return new Token(myString,Type.str_char);
            }
            // fall through
          case 26: break;
          case 10:
            { myCharacter+=yytext();yybegin(ENDOFCHAR);
            }
            // fall through
          case 27: break;
          case 11:
            { yybegin(YYINITIAL);myCharacter +=yytext();return new Token(myCharacter,Type.undefind);
            }
            // fall through
          case 28: break;
          case 12:
            { yybegin(YYINITIAL);myCharacter +="\'";  return new Token(myCharacter,Type.str_char);
            }
            // fall through
          case 29: break;
          case 13:
            { yybegin(YYINITIAL);return new Token(yytext(),Type.comment);
            }
            // fall through
          case 30: break;
          case 14:
            { yybegin(YYINITIAL);return new Token(yytext(),Type.real);
            }
            // fall through
          case 31: break;
          case 15:
            { yybegin(YYINITIAL);return new Token(yytext(),Type.spChar);
            }
            // fall through
          case 32: break;
          case 16:
            { myString+=yytext();return new Token(yytext(),Type.spChar);
            }
            // fall through
          case 33: break;
          case 17:
            { myCharacter+=yytext();yybegin(ENDOFCHAR);return new Token(yytext(),Type.spChar);
            }
            // fall through
          case 34: break;
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
