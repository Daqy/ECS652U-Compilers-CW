/**
 * Define a grammar for Cool
 */
parser grammar CoolParser;

options { tokenVocab = CoolLexer; }


/*  Starting point for parsing a Cool file  */

program : (coolClass SEMICOLON)+ EOF;

coolClass :  CLASS TYPE (INHERITS TYPE)? LBRACE (feature SEMICOLON)* RBRACE;

feature : ID LPAREN (formal (COMMA formal)*)? RPAREN COLON TYPE LBRACE expr RBRACE
        | ID COLON TYPE (ASSIGN expr)?;

formal  : ID COLON TYPE;

expr    : ID ASSIGN expr
        | expr (AT TYPE)? DOT ID LPAREN (expr (COMMA expr)*)? RPARENT
        | ID LPAREN (expr (COMMA expr)*)?
        | IF expr THEN expr ELSE expr FI
        | WHILE expr LOOP expr POOL
        | LBRACE (expr SEMICOLON)+ RBRACE
        | LET ID COLON TYPE (ASSIGN expr)? (COMMA ID COLON TYPE (ASSIGN expr )?)* IN expr
        | CASE expr OF (ID COLON TYPE RIGHTARROW expr SEMICOLON )+ ESAC
        | NEW TYPE
        | ISVOID expr
        | expr PLUS expr
        | expr MINUS expr
        | expr MULT expr
        | expr DIV expr
        | TILDE expr
        | expr LESSTHAN expr
        | expr LEFTARROW expr
        | expr EQUAL expr
        | NOT expr
        | LPAREN expr RPAREN
        | ID
        | INT
        | STRING
        | BOOL;

// program ::= [class; ]+
// class ::= class TYPE [inherits TYPE] { [feature; ]∗ } 
//feature ::= ID( [ formal [, formal]∗ ] ) : TYPE { expr }
// | ID:TYPE [ <- expr ] 
//formal ::= ID : TYPE
//expr ::= ID <- expr
// | expr[@TYPE].ID( [ expr [, expr]∗ ] ) | ID( [ expr [, expr]∗ ] )
// | if expr then expr else expr fi
// | while expr loop expr pool
// | { [expr; ]+ }
// | let ID:TYPE [ <- expr ] [,ID:TYPE [ <- expr ]]∗ in expr | case expr of [ID : TYPE => expr; ]+esac
// | new TYPE
// | isvoid expr
// | expr + expr
// | expr − expr
// | expr ∗ expr
// | expr / expr
// |  ̃expr
// | expr < expr
// | expr <= expr
// | expr = expr
// | not expr
// | (expr)
// | ID
// | integer
// | string
// | true
// | false