/**
 * Define a lexer rules for Cool
 */
lexer grammar CoolLexer;
@members {
    StringBuilder buf;
    int count = 0;
}

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
CLASS       : ('c'|'C') ('l'|'L') ('a'|'A') ('s'|'S') ('s'|'S') ;
ELSE        : 'else';
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

fragment TRUE        : 'true';
fragment FALSE       : 'false';
BOOL        :(TRUE|FALSE);

/* INT */
fragment DIGIT      : [0-9];
INT : DIGIT+ ;

FAILEDESNULLSTRING : '"' .*? '\\''\u0000' .*? '"' {setText("String contains escaped null character."); setType(ERROR);};
FAILEDNULLSTRING : '"' .*? '\u0000' .*? '"' {setText("String contains null character."); setType(ERROR);};
// NULLSTRING:  ('->''\u0000') {setText("String contains escaped null character."); setType(ERROR);};
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

/* Line comment */


/* Block comment */
// Escape : (' ' | '\n' | '\r' | '\t' | '\u000B')+ -> skip;

NEWLINE : ('\n'|'\r') -> skip;
WHITESPACE  : [\t\b\f\r\n \u000B] -> skip;
// WHITESPACE :(' ' | '\n' | '\r' | '\t' | '\u000B')+ -> skip;
STRING : '"' { buf = new StringBuilder(); } -> more, pushMode(STRING_MODE);
COMMENT : '--' -> pushMode(LINECOMMENT), skip;
// BLOCKCOMMENT : '(*' -> pushMode(BLOCKCOMMENT_MODE), skip;
BLOCKCOMMENT : '(*' {count+=1;} -> pushMode(BLOCKCOMMENT_MODE), skip;

UNMATCHED: '*)' {setText("Unmatched *)"); setType(ERROR); };

ERROR : . ;

mode LINECOMMENT;
EOFLINE : (EOF | '\n') -> popMode, skip;
VALIDCOMMENT : . -> more;

mode STRING_MODE;

STR_CONST : '"' {if (buf.toString().length() > 1024) {setText("String constant too long"); setType(ERROR);} else {setText(('"' + buf.toString() + '"'));}} -> popMode;
STRING_ESCAPE_SEQUENCE :
    '\\'  (
      'n' { buf.append('\n'); } 
      | 't' { buf.append('\t'); }
      | 'b' { buf.append('\b'); }
      | 'f' { buf.append('\f'); }
      | 'r' { buf.append("015"); }
      | [0-9] { buf.append(getText().substring(2)); }
      | . { buf.append(getText().substring(1)); }) -> more;

NULL_STRING : [\u0000] {setText("String contains null character."); setType(ERROR);} -> popMode;
UNTERIMANTED : [\n]* {setText("Unterminated string constant"); setType(ERROR);} -> popMode;
EOFSTRING : (EOF) {setText("EOF in string constant"); setType(ERROR);} -> popMode;
STRING_VALID_CHAR : . { buf.append(getText()); } -> more;

mode BLOCKCOMMENT_MODE;

EOFCOMMENT: .(EOF) {setText("EOF in comment"); setType(ERROR);} -> popMode;
STARTCOMMENT : '(*' {count+=1;} -> pushMode(BLOCKCOMMENT_MODE), skip;
ENDCOMMENT : '*)' {count-=1;} -> popMode, skip;
CORRECTBLOCKCOMMENT : . -> skip;
EOFBRACKET: '*)' (EOF) {count -=1; if (count>0) {setText("EOF in comment"); setType(ERROR);} else {skip();}} -> popMode;