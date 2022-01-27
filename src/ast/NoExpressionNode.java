package ast;

import ast.visitor.TreeVisitor;

public class NoExpressionNode extends ExpressionNode {

    public NoExpressionNode(int lineNumber) {
        super(0);
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
