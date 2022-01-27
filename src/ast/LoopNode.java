package ast;

import ast.visitor.TreeVisitor;

public class LoopNode extends ExpressionNode {

    protected ExpressionNode cond;

    public ExpressionNode getCond() {
        return cond;
    }

    public ExpressionNode getBody() {
        return body;
    }

    protected ExpressionNode body;

    public LoopNode(int lineNumber, ExpressionNode cond, ExpressionNode body) {
        super(lineNumber);
        this.cond = cond;
        this.body = body;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
