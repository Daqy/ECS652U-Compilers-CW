package ast;

import ast.visitor.TreeVisitor;

public class NegNode extends IntUnopNode {

    public NegNode(int lineNumber, ExpressionNode e1) {
        super(lineNumber, e1);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
