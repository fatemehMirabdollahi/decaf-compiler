

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
%%



<<EOF>>    {return new Token("$");}