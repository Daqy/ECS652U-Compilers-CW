package ast;

import ast.visitor.TreeVisitor;

public class BoolConstNode extends ConstNode<Boolean> {

    public BoolConstNode(int lineNumber, Boolean val) {
        super(lineNumber, val);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
