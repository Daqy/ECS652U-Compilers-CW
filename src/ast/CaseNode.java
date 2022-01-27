package ast;

import ast.visitor.TreeVisitor;

import java.util.List;

public class CaseNode extends ExpressionNode {
    protected ExpressionNode expr;

    public ExpressionNode getExpr() {
        return expr;
    }

    public List<BranchNode> getCases() {
        return cases;
    }

    protected List<BranchNode> cases;


    public CaseNode(int lineNumber, ExpressionNode expr, List<BranchNode> cases) {
        super(lineNumber);
        this.expr = expr;
        this.cases = cases;
        this.expr = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
