package ast;

import ast.visitor.TreeVisitor;

public abstract class ConstNode<T> extends ExpressionNode {

    protected T val;

    public T getVal() {
        return val;
    }

    protected ConstNode(int lineNumber, T val) {
        super(lineNumber);
        this.val = val;
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
