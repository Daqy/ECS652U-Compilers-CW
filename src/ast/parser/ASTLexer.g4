/**
 * Define a grammar for textual output of AST trees
 */
lexer grammar ASTLexer;

@members {
    StringBuilder buf;
}

TYPE        : [A-Z] IDENTIFIER* ;

ID          : [a-z] IDENTIFIER* ;

fragment
IDENTIFIER  : [a-zA-Z0-9_]      ;

INT_CONST   : [0-9]+        ;

LINENO      : '#' [0-9]+ { setText(getText().substring(1)); } ;

PROGRAM     : '_program'    ;
CLASS       : '_class'      ;
METHOD      : '_method'     ;
ATTR        : '_attr'       ;
FORMAL      : '_formal'     ;
BRANCH      : '_branch'     ;
ASSIGN      : '_assign'     ;
STATIC_DISPATCH : '_static_dispatch' ;
DISPATCH    : '_dispatch'   ;
COND        : '_cond'       ;
LOOP        : '_loop'       ;
TYPCASE     : '_typcase'    ;
BLOCK       : '_block'      ;
LET         : '_let'        ;
PLUS        : '_plus'       ;
SUB         : '_sub'        ;
MUL         : '_mul'        ;
DIVIDE      : '_divide'     ;
NEG         : '_neg'        ;
LT          : '_lt'         ;
EQ          : '_eq'         ;
LEQ         : '_leq'        ;
COMP        : '_comp'       ;
INT         : '_int'        ;
STR         : '_string'     ;
BOOL        : '_bool'       ;
NEW         : '_new'        ;
ISVOID      : '_isvoid'     ;
NO_EXPR     : '_no_expr'    ;
NO_TYPE     : '_no_type'    ;
OBJECT      : '_object'     ;

COLON       : ':'           ;
LPAREN      : '('           ;
RPAREN      : ')'           ;

WHITESPACE  : [ \t\b\f\r\n]     -> skip;

STRING_OPEN : '"'   { buf = new StringBuilder(); }
            -> more, pushMode(STRING_MODE);

mode STRING_MODE;

STR_CONST   : '"'
    {
        setText(buf.toString());
    }
    -> popMode;

STRING_ESCAPE_SEQUENCE :
    '\\'
    (
        'n'             { buf.append('\n'); }
     |  't'             { buf.append('\t'); }
     |  'b'             { buf.append('\b'); }
     |  'f'             { buf.append('\f'); }
     |  'u'([0-9])+     { buf.append((char)Integer.parseInt(getText().substring(2), 16)); }
     |   .              { buf.append(getText().substring(1)); }
    )
    -> more;


STRING_VALID_CHAR   : .  { buf.append(getText()); }
    -> more;
