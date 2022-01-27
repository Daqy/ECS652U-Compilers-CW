package ast;

import ast.visitor.TreeVisitor;

public class LetNode extends ExpressionNode {
    protected Symbol identifier;
    protected Symbol type_decl;

    public Symbol getIdentifier() {
        return identifier;
    }

    public Symbol getType_decl() {
        return type_decl;
    }

    public ExpressionNode getInit() {
        return init;
    }

    public ExpressionNode getBody() {
        return body;
    }

    protected ExpressionNode init;
    protected ExpressionNode body;

    public LetNode(int lineNumber, Symbol id, Symbol type_dec, ExpressionNode init, ExpressionNode body) {
        super(lineNumber);
        this.identifier = id;
        this.type_decl = type_dec;
        this.init = init;
        this.body = body;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
