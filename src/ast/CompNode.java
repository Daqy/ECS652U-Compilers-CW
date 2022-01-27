package ast;

import ast.visitor.TreeVisitor;

public class CompNode extends BoolUnopNode {

    public CompNode(int lineNumber, ExpressionNode e1) {
        super(lineNumber, e1);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
