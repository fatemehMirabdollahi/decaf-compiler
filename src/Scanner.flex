

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
spChar = (\\n|\\r|\\t|\\d|\\b)
starCom = "/*"~"*/"
singleCom = "//" [^\r\n]*{enter}?
InputCharacter = [^\r\n]
op = ("+" | "*" | "<" | ">" | "&" | \" | "!" | "," | "[" | "\(" | "\-" | "/" | "%" | "\|" | "^" | "." | ";" | "]" | ")" |"+=" | "*=" | "++" | "!=" | "==" | "&&" | "-=" | "/=" | "--" | "<=" | ">=" | "==" | "\|\|")
string = \"~ \"
char = [\'][spChar][\']
%state SPECIAL
%state STRING
%state CHARACTER
%state ENDOFCHAR
%state SPACE
%%

<YYINITIAL>{
     \"                        {yybegin(STRING);myString="\"";}
     \'                        {yybegin(CHARACTER);myCharacter="\'";}
    {starCom}                  {yybegin(SPACE);token = new Token(yytext(),Type.comment);}
    {singleCom}                {yybegin(YYINITIAL); endl=true;return new Token(yytext(),Type.comment);}
    {keyword}                  {yybegin(SPACE);token = new Token(yytext(),Type.keyword);}
    {id}                       {yybegin(SPACE);token = new Token(yytext(),Type.id);}
    {sci}                      {yybegin(SPACE);token = new Token(yytext(),Type.real);}
    {hex}                      {yybegin(SPACE);token = new Token(yytext(),Type.integer);}
    {real}                     {yybegin(SPACE);token = new Token(yytext(),Type.real);}
    {int}                      {yybegin(SPACE);token = new Token(yytext(),Type.integer);}
    {op}                       {yybegin(SPACE);token = new Token(yytext(),Type.op_punc);}
    '~'                        {return new Token(yytext(),Type.undefind);}
}
<STRING>{
    "\""                       {yybegin(SPACE); myString+="\""  ;token = new Token(myString,Type.str_char); }
    {InputCharacter}           {myString+=yytext();}
    {spChar}                   {myString+="<i>"+yytext()+"</i>";}
}

<CHARACTER>{
    {spChar}                    {myCharacter+="<i>"+yytext()+"</i>";yybegin(ENDOFCHAR);}
    {InputCharacter}            {myCharacter+=yytext();yybegin(ENDOFCHAR);}
}
<ENDOFCHAR>{
    \'                          {yybegin(SPACE);myCharacter +="\'";  token = new Token(myCharacter,Type.str_char);}
}
<SPACE>{
    \s                          {yybegin(YYINITIAL); return token;}
    {enter}                     {yybegin(YYINITIAL); endl=true;return token;}
    <<EOF>>                     {yybegin(YYINITIAL); return token;}
    [^\s]+                      {yybegin(YYINITIAL);System.out.println("hii");return new Token(token.getValue()+yytext(),Type.undefind);}
    ^{enter}+                   {yybegin(YYINITIAL);System.out.println("hii");return new Token(token.getValue()+yytext(),Type.undefind);}

}
{enter}                         {yybegin(YYINITIAL); endl=true;/*return new Token(yytext(),Type.undefind);*/}
\s                              {yybegin(YYINITIAL);/*return new Token(yytext(),Type.undefind);*/}

[^]                             {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
<<EOF>>                         {return new Token("$");}
