package ast;

import ast.visitor.TreeVisitor;

public class AssignNode extends ExpressionNode {
    protected Symbol name;

    public Symbol getName() {
        return name;
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    protected ExpressionNode expr;

    public AssignNode(int lineNumber, Symbol name, ExpressionNode expr) {
        super(lineNumber);
        this.name = name;
        this.expr = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
