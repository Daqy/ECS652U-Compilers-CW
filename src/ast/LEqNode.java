package ast;

import ast.visitor.TreeVisitor;

public class LEqNode extends BoolBinopNode {

    public LEqNode(int lineNumber, ExpressionNode e1, ExpressionNode e2) {
        super(lineNumber, e1, e2);
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
