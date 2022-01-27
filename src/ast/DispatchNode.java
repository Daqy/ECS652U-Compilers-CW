package ast;

import ast.visitor.TreeVisitor;

import java.util.List;

public class DispatchNode extends ExpressionNode {
    protected ExpressionNode expr;
    protected Symbol name;

    public ExpressionNode getExpr() {
        return expr;
    }

    public Symbol getName() {
        return name;
    }

    public List<ExpressionNode> getActuals() {
        return actuals;
    }

    protected List<ExpressionNode> actuals;

    public DispatchNode(int lineNumber,
                           ExpressionNode expr, Symbol name, List<ExpressionNode> actuals) {
        super(lineNumber);
        this.expr = expr;
        this.name = name;
        this.actuals = actuals;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }

}
