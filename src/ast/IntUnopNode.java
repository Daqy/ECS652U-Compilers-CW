package ast;

import ast.visitor.TreeVisitor;

public abstract class IntUnopNode extends UnopNode {

    protected IntUnopNode(int lineNumber, ExpressionNode e1) {
        super(lineNumber, e1);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
