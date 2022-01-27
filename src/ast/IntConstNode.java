package ast;

import ast.visitor.TreeVisitor;

public class IntConstNode extends ConstNode<Symbol> {

    public IntConstNode(int lineNumber, Symbol val) {
        super(lineNumber, val);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
