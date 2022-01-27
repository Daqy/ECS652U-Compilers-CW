package ast;

import ast.visitor.TreeVisitor;

public abstract class FeatureNode extends TreeNode {
    protected FeatureNode(int lineNumber) {
        super(lineNumber);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
