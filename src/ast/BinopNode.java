package ast;

import ast.visitor.TreeVisitor;

public abstract class BinopNode extends ExpressionNode {

    protected ExpressionNode e1;
    protected ExpressionNode e2;

    public ExpressionNode getE1() {
        return e1;
    }

    public ExpressionNode getE2() {
        return e2;
    }

    public BinopNode(int lineNumber, ExpressionNode e1, ExpressionNode e2) {
        super(lineNumber);
        this.e1 = e1;
        this.e2 = e2;
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
