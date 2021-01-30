package scanner;
%%
%public
%class DecafScanner
%standalone
%function tokenReader
%type Token
%unicode
%{
public String myString = "";
public Token token ;
public boolean endl =false;
public static String value;
%}

keyword = (void | int | double | bool | string | record | for| while | if | else | return | break | new | NewArray | Print | ReadInteger | ReadLine | continue | function | main| true | false)
id = [a-zA-Z][a-zA-Z0-9_]{0,30}
int = [0-9]+
real = [0-9]+[.][0-9]*
hex = 0[xX][0-9a-fA-F]+
sci = [0-9]+[.][0-9]*E[-\+]{0,1}[0-9]+
enter = (\r\n|\n|\r)
spChar = (\\n|\\r|\\t|\\d|\\b|\\\'|\\\"|\\)
starCom = "/*"~"*/"
singleCom = "//" [^\r\n]*{enter}?
InputCharacter = [^\r\n]
op = ("+"|"=" |"{"|"}" |"*" | "<" | ">" | "&" | \" | "!" | ":"| "[" | "\(" | "\-" | "/" | "%" | "\|" | "^" | "." | ";" | "]" | ")" |"+=" | "*=" | "++" | "!=" | "==" | "&&" | "-=" | "/=" | "--" | "<=" | ">=" | "==" | "\|\|")
char = [\']{1} [^\r\n]{1}[\']{1}
charSp = [\']{1} {spChar}{1}[\']{1}

%state STRING

%%
<YYINITIAL>{
    {char}                     {yybegin(YYINITIAL);value = yytext(); return new Token("str",TokenType.str_char);}
    {charSp}                   {yybegin(YYINITIAL);return new Token(yytext() ,TokenType.str_char);}
    \"                         {yybegin(STRING);myString="\"";}
    {starCom}                  {yybegin(YYINITIAL);}
    {singleCom}                {yybegin(YYINITIAL);}
    {keyword}                  {yybegin(YYINITIAL); return new Token(yytext(),TokenType.keyword);}
    {id}                       {yybegin(YYINITIAL);value = yytext(); return new Token("ident",TokenType.id);}
    {real}                     {yybegin(YYINITIAL);value = yytext();return new Token("rcv",TokenType.real);}
    {int}                      {yybegin(YYINITIAL);value = yytext();return new Token("icv",TokenType.integer);}
    {op}                       {yybegin(YYINITIAL);return new Token(yytext(),TokenType.op_punc);}
    ,                          {yybegin(YYINITIAL);return new Token("colon",TokenType.op_punc);}
    '~'                        {return new Token(yytext(),TokenType.undefined);}
}
<STRING>{
    "\""                       {yybegin(YYINITIAL); myString+="\""  ;value = myString; return new Token("str",TokenType.str_char); }
    {spChar}                   {myString+=yytext();}
    {InputCharacter}           {myString+=yytext();}
}

{enter}                         {yybegin(YYINITIAL); endl=true;}

\s                              {yybegin(YYINITIAL); }

[^]                             {yybegin(YYINITIAL);return new Token(yytext(),TokenType.undefined);}
<<EOF>>                         {return new Token("$");}
