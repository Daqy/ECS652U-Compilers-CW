package ast;

import ast.visitor.TreeVisitor;

public abstract class UnopNode extends ExpressionNode {

    protected ExpressionNode e1;

    protected UnopNode(int lineNumber, ExpressionNode e1) {
        super(lineNumber);
        this.e1 = e1;
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }

    public ExpressionNode getE1() {
        return e1;
    }
}
