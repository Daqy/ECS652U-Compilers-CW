package ast;

import ast.visitor.TreeVisitor;

public class CondNode extends ExpressionNode {

    protected ExpressionNode cond;
    protected ExpressionNode thenExpr;
    protected ExpressionNode elseExpr;

    public ExpressionNode getCond() {
        return cond;
    }

    public ExpressionNode getThenExpr() {
        return thenExpr;
    }

    public ExpressionNode getElseExpr() {
        return elseExpr;
    }

    public CondNode(int lineNumber, ExpressionNode cond, ExpressionNode thenE, ExpressionNode elseE) {
        super(lineNumber);
        this.cond = cond;
        this.thenExpr = thenE;
        this.elseExpr = elseE;
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
