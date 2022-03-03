/**
 * Define a grammar for Cool
 */
parser grammar CoolParser;

options { tokenVocab = CoolLexer; }

/*  Starting point for parsing a Cool file  */

program : (coolClass SEMICOLON)+ EOF;

coolClass : CLASS TYPE (INHERITS TYPE)? LBRACE (feature SEMICOLON)* RBRACE;

feature : method | attribute;

method : ID LPAREN (formal (COMMA formal)*)? RPAREN COLON TYPE LBRACE expr RBRACE;

attribute : ID COLON TYPE (ASSIGN expr)?;

formal  : ID COLON TYPE;

expr    : ID ASSIGN expr #assignmentExpression
        | expr DOT ID LPAREN (expr (COMMA expr)*)? RPAREN #atDynamicExpression
        | expr AT TYPE DOT ID LPAREN (expr (COMMA expr)*)? RPAREN #atStaticExpression
        | ID LPAREN (expr (COMMA expr)*)? RPAREN #idBraceExpression
        | IF expr THEN expr ELSE expr FI #condExpression
        | WHILE expr LOOP expr POOL #whileExpression
        | LBRACE (expr SEMICOLON)+ RBRACE #blockExpression
        | LET ID COLON TYPE (ASSIGN expr)? (COMMA ID COLON TYPE (ASSIGN expr )?)* IN expr #letExpression
        | CASE expr OF specialCase+ ESAC #caseExpression
        | NEW TYPE #newExpression
        | ISVOID expr #voidExpression
        | expr PLUS expr #plusExpression
        | expr MINUS expr #minusExpression
        | expr MULT expr #multiplyExpression
        | expr DIV expr #divideExpression
        | TILDE expr #tildeExpression
        | expr LESSTHAN expr #lessThanExpression
        | expr LEFTARROW expr #leftArrowExpression
        | expr EQUAL expr #equalExpression
        | NOT expr #notExpression
        | LPAREN expr RPAREN #bracketExpression
        | ID #idExpression
        | INT #integerExpression
        | STR_CONST #stringExpression
        | BOOL #booleanExpression;

specialCase : ID COLON TYPE RIGHTARROW expr SEMICOLON;

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