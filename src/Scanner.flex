

%%

%public
%class DecafScanner
%standalone
%function tokenReader
%type Token
%unicode
%{
String myString = "";
String myCharacter = "";
%}
keyword = (void | int | double | bool | string | record | for| while | if | else | return | break | new | NewArray | Print | ReadInteger | ReadLine | continue | false | true)

id = [a-zA-Z][a-zA-Z0-9_]{0,30}
int = [0-9]+
real = [0-9]+[.][0-9]*
hex = 0[xX][0-9a-fA-F]+
sci = [0-9]+[.][0-9]*E[-\+]{0,1}[0-9]+
enter = (\r\n|\n|\r)
spChar = (\\n|\\r|\\t|\\d|\\b)
starCom = "/*"~"*/"
singleCom = "//" {InputCharacter}* {enter}?
InputCharacter = [^enter]
op = ("+" | "*" | "<" | ">" | "&" | \" | "!" | "," | "[" | "\(" | "\-" | "/" | "%" | "\|" | "^" | "." | ";" | "]" | ")" |"+=" | "*=" | "++" | "!=" | "==" | "&&" | "-=" | "/=" | "--" | "<=" | ">=" | "==" | "\|\|")
string = \"~ \"
char = [\'][spChar][\']
%state SPECIAL
%state STRING
%state CHARACTER
%state ENDOFCHAR
%%

<YYINITIAL>{
     \"                        {yybegin(STRING);myString="\"";}
     \'                         {yybegin(CHARACTER);myCharacter="\'";}
    {starCom}                  {yybegin(YYINITIAL);return new Token(yytext(),Type.comment);}
    {singleCom}                {yybegin(YYINITIAL);return new Token(yytext(),Type.comment);}
    {id}                       {return new Token(yytext(),Type.id);}
    {sci}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.real);}
    {hex}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.integer);}
    {real}                     {yybegin(YYINITIAL);return new Token(yytext(),Type.real);}
    {int}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.integer);}
    {spChar}                   {yybegin(YYINITIAL);return new Token(yytext(),Type.spChar);}
    {op}                       {yybegin(YYINITIAL);return new Token(yytext(),Type.op_punc);}
    '~'                         {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
}
<STRING>{
    "\""                       {yybegin(YYINITIAL); myString+="\""  ;return new Token(myString,Type.str_char); }
    {InputCharacter}           {myString+=yytext();}
    {spChar}                   {myString+=yytext();return new Token(yytext(),Type.spChar);}
}

<CHARACTER>{
    {spChar}                    {myCharacter+=yytext();yybegin(ENDOFCHAR);return new Token(yytext(),Type.spChar);}
    {InputCharacter}            {myCharacter+=yytext();yybegin(ENDOFCHAR);}
}
<ENDOFCHAR>{
    \'                           {yybegin(YYINITIAL);myCharacter +="\'";  return new Token(myCharacter,Type.str_char);}
}
{enter}                        {yybegin(YYINITIAL);/*return new Token(yytext(),Type.undefind);*/}
[^]                            {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
<<EOF>>                        {return new Token("$");}
