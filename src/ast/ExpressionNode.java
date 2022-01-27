package ast;

import ast.visitor.TreeVisitor;

public abstract class ExpressionNode extends TreeNode {
    protected Symbol type;

    public Symbol getType() {
        return type;
    }

    public void setType(Symbol type) {
        this.type = type;
    }

    protected ExpressionNode(int lineNumber) {
        super(lineNumber);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
