%%
%public
%class DecafScanner
%standalone
%function tokenReader
%type Token
%unicode
%{
String myString = "";
Token token ;
boolean endl =false;
%}

keyword = (void | int | double | bool | string | record | for| while | if | else | return | break | new | NewArray | Print | ReadInteger | ReadLine | continue | false | true)
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
op = ("+"|"=" |"{"|"}" |"*" | "<" | ">" | "&" | \" | "!" | ":"| "," | "[" | "\(" | "\-" | "/" | "%" | "\|" | "^" | "." | ";" | "]" | ")" |"+=" | "*=" | "++" | "!=" | "==" | "&&" | "-=" | "/=" | "--" | "<=" | ">=" | "==" | "\|\|")
char = [\']{1} [^\r\n]{1}[\']{1}
charSp = [\']{1} {spChar}{1}[\']{1}

%state STRING

%%
<YYINITIAL>{
    {char}                     {yybegin(YYINITIAL);return new Token(yytext(),Type.str_char);}
    {charSp}                   {yybegin(YYINITIAL);return new Token(yytext().charAt(0) + "<i>"+yytext().substring(1,3)+"</i>"+ yytext().charAt(3) ,Type.str_char);}
    \"                         {yybegin(STRING);myString="\"";}
    {starCom}                  {yybegin(YYINITIAL);return new Token(yytext(),Type.comment);}
    {singleCom}                {yybegin(YYINITIAL); endl=true;return new Token(yytext(),Type.comment);}
    {keyword}                  {yybegin(YYINITIAL);return new Token(yytext(),Type.keyword);}
    {id}                       {yybegin(YYINITIAL);return new Token(yytext(),Type.id);}
    {sci}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.real);}
    {hex}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.integer);}
    {real}                     {yybegin(YYINITIAL);return new Token(yytext(),Type.real);}
    {int}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.integer);}
    {op}                       {yybegin(YYINITIAL);return new Token(yytext(),Type.op_punc);}
    '~'                        {return new Token(yytext(),Type.undefined);}
}
<STRING>{
    "\""                       {yybegin(YYINITIAL); myString+="\""  ;return new Token(myString,Type.str_char); }
    {spChar}                   {myString+="<i>"+yytext()+"</i>";}
    {InputCharacter}           {myString+=yytext();}
}

{enter}                         {yybegin(YYINITIAL); endl=true;}

\s                              {yybegin(YYINITIAL); }

[^]                             {yybegin(YYINITIAL);return new Token(yytext(),Type.undefined);}
<<EOF>>                         {return new Token("$");}
