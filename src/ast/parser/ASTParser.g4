/**
 * Define a grammar for textual output of AST trees
 */
parser grammar ASTParser;

options { tokenVocab = ASTLexer; }


program
	: LINENO PROGRAM (coolClass)+ EOF
    ;

coolClass
	: LINENO CLASS type type STR_CONST LPAREN (feature)* RPAREN
	;

feature
	: attr
	| method
	;

method
    : LINENO METHOD id (formal)* type expr
    ;

attr
    : LINENO ATTR id type expr
    ;

formal
	: LINENO FORMAL id type
	;

expr
	: expr_aux COLON type
	| expr_aux COLON NO_TYPE
	;

expr_aux
	: LINENO ASSIGN id expr                                 # Assign
    | LINENO STATIC_DISPATCH expr type id LPAREN (expr)*  RPAREN         # StaticDispatch
    | LINENO DISPATCH expr id LPAREN (expr)* RPAREN         # DynamicDispatch
    | LINENO COND expr expr expr                            # Cond
    | LINENO LOOP expr expr                                 # Loop
    | LINENO BLOCK (expr)+                                  # Block
    | LINENO LET id type expr expr                          # Let
    | LINENO TYPCASE expr (simplecase)+                     # Case
    | LINENO NEW type                                       # New
    | LINENO ISVOID expr                                    # Isvoid
    | LINENO PLUS expr expr                                 # Binop
    | LINENO SUB expr expr                                  # Binop
    | LINENO MUL expr expr                                  # Binop
    | LINENO DIVIDE expr expr                               # Binop
    | LINENO NEG expr                                       # Unop
    | LINENO LT expr expr                                   # Binop
    | LINENO EQ expr expr                                   # Binop
    | LINENO LEQ expr expr                                  # Binop
    | LINENO COMP expr                                      # Unop
    | LINENO INT INT_CONST                                  # ConstInt
    | LINENO STR STR_CONST                                  # ConstStr
    | LINENO BOOL INT_CONST                                 # ConstBool
    | LINENO OBJECT id                                      # Obj
    | LINENO NO_EXPR                                        # NoExpr
    ;


simplecase
    : LINENO BRANCH id type expr
    ;

type
    : TYPE
    ;

id  : ID
    ;

