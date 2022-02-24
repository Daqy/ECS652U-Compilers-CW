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

/* Keyword 
The keywords of cool are: class, else, false, fi, if, in, inherits, isvoid, let, loop, pool, then, while,
case, esac, new, of, not, true.
*/
CLASS       : 'class';
ELSE        : 'else';
FALSE       : 'false';
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
TRUE        : 'true';

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

/* INT */
fragment DIGIT      : [0-9];
INT : DIGIT+ ;

/* String */
fragment LETTER     : [a-zA-Z];

/* OBJECT * TYPE ID
 Type identifiers begin with a capital letter; object identifiers
begin with a lower case letter.
 */
fragment IDENTIFIER  : [a-zA-Z0-9_];
ID        : [a-z] IDENTIFIER* ;
TYPE        : [A-Z] IDENTIFIER* ;

/* Escape characters */
BOOL        :(TRUE|FALSE);
/* Line comment */


/* Block comment */
// Escape : (' ' | '\n' | '\r' | '\t' | '\u000B')+ -> skip;



NEWLINE : ('\n'|'\r') -> skip;
WHITESPACE  : [\t\b\f\r\n ] -> skip;

UNTERIMATEDERROR : '"'(.)*? [\n]'"' {System.out.println("Unterminated string constant");};
STRING : '"' (.)*? '"';
COMMENT : '--' (.)*? ('\n') -> skip;
BLOCKCOMMENT : '(*' (.|WHITESPACE)*? '*)' -> skip;

ERROR : . ;