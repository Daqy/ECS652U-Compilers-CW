package ast;

import ast.visitor.TreeVisitor;

import java.util.List;

public class BlockNode extends ExpressionNode {
    protected List<ExpressionNode> exprs;

    public List<ExpressionNode> getExprs() {
        return exprs;
    }

    public BlockNode(int lineNumber, List<ExpressionNode> exprs) {
        super(lineNumber);
        this.exprs = exprs;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
