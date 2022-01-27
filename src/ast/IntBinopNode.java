package ast;

import ast.visitor.TreeVisitor;

public abstract class IntBinopNode extends BinopNode {

    protected IntBinopNode(int lineNumber, ExpressionNode e1, ExpressionNode e2) {
        super(lineNumber, e1, e2);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
