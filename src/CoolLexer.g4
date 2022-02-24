/**
 * Define a lexer rules for Cool
 */
lexer grammar CoolLexer;

/* Punctution */

DOT                 : '.';
COMMA               : ',';
AT                  : '@';
SEMICOLON           : ';';
COLON               : ':';

LBRACE              : '{' ;
RBRACE              : '}' ;
LPAREN              : '(' ;
RPAREN              : ')' ;

/* Operators */

PLUS       : '+';
MINUS      : '-';
MULT       : '*';
DIV        : '/';

TILDE     : '~';

LESSTHAN               : '<';
LEFTARROW              : '<=';
EQUAL                  : '=' ;
ASSIGN                 : '<-';
RIGHTARROW             : '=>';

ERROR : . ;

/* INT */
fragment DIGIT      : [0-9];
INT : DIGIT+ ;

/* String */
fragment LETTER     : [a-zA-Z];
fragment UPPER     : [A-Z];
fragment LOWER     : [a-z];
ID  : LETTER (LETTER|DIGIT)*;

/* Whitespace */
WHITESPACE :(' ' | '\n' | '\r' | '\t' | '\u000B')+ -> skip;


/* Keyword 
The keywords of cool are: class, else, false, fi, if, in, inherits, isvoid, let, loop, pool, then, while,
case, esac, new, of, not, true.
*/
CLASS       : 'class';
ELSE        : 'else';
FALSE       : 'False';
FI          : 'fi';
IF          : 'if';
IN          : 'in';
INHERITS    : 'inherits';
ISVOID      : 'isvoid';
LET         : 'let';
LOOP        : 'loop';
POOL        : 'pool';
THEN        : 'then';
WHILE       : 'while';
CASE        : 'case';
ESAC        : 'esac';
NEW         : 'new';
OF          : 'of';
NOT         : 'not';
TRUE        : 'True';



BOOL        :(TRUE|FALSE);

/* OBJECT * TYPE ID
 Type identifiers begin with a capital letter; object identifiers
begin with a lower case letter.
 */
OBJECT      :(LOWER)(LETTER);
TYPE        :(UPPER)(LETTER);


/* Escape characters */

/* Line comment */
COMMENT : '/*' .*? '*/' -> skip;

/* Block comment */