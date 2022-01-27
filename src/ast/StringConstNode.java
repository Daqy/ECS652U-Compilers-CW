package ast;

import ast.visitor.TreeVisitor;

public class StringConstNode extends ConstNode<Symbol> {

    public StringConstNode(int lineNumber, Symbol val) {
        super(lineNumber, val);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
