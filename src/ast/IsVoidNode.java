package ast;

import ast.visitor.TreeVisitor;

public class IsVoidNode extends ExpressionNode {
    public ExpressionNode getE1() {
        return e1;
    }

    protected ExpressionNode e1;

    public IsVoidNode(int lineNumber, ExpressionNode expr) {
        super(lineNumber);
        this.e1 = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
