package ast;

import ast.visitor.TreeVisitor;

public class BranchNode extends TreeNode {


    protected Symbol name;
    protected Symbol type_decl;
    protected ExpressionNode expr;

    public BranchNode(int lineNumber, Symbol name, Symbol type_decl, ExpressionNode expr) {
        super(lineNumber);
        this.name = name;
        this.type_decl = type_decl;
        this.expr = expr;
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }

    public Symbol getName() {
        return name;
    }

    public Symbol getType_decl() {
        return type_decl;
    }

    public ExpressionNode getExpr() {
        return expr;
    }
}
