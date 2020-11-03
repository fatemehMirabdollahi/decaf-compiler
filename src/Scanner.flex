

%%

%public
%class DecafScanner
%standalone
%function tokenReader
%type Token
%unicode

keyword = (void | int | double | bool | string | record
     | for | while | if | else | return | break
     | new | NewArray | Print | ReadInteger | ReadLine
     | continue | false | true)

id = [a-zA-Z][a-zA-Z0-9_]{0,30}
int = [0-9]+
real = [0-9]+[.][0-9]*
hex = 0[xX][0-9a-fA-F]+
sci = [0-9]+[.][0-9]*E[-\+]{0,1}[0-9]+
enter = (\n\r|\n|\r)


%%

<YYINITIAL>{
    {id}                       {return new Token(yytext(),Type.id);}
    {sci}[a-zA-Z][a-zA-Z0-9]*  {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
    {hex}[g-zG-Z][a-zA-Z0-9]*  {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
    {sci}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.real);}
    {hex}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.integer);}
    {real}[a-zA-Z][a-zA-Z0-9]* {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
    {int}[a-zA-Z][a-zA-Z0-9]*  {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
    {real}                     {yybegin(YYINITIAL);return new Token(yytext(),Type.real);}
    {int}                      {yybegin(YYINITIAL);return new Token(yytext(),Type.integer);}

}
{enter}                 {yybegin(YYINITIAL);/*return new Token(yytext(),Type.undefind);*/}
[^]                         {yybegin(YYINITIAL);return new Token(yytext(),Type.undefind);}
<<EOF>>    {return new Token("$");}