package ast;

import ast.visitor.TreeVisitor;

import java.util.List;

public class StaticDispatchNode extends ExpressionNode {
    protected ExpressionNode expr;

    public ExpressionNode getExpr() {
        return expr;
    }

    public Symbol getType_name() {
        return type_name;
    }

    public Symbol getName() {
        return name;
    }

    public List<ExpressionNode> getActuals() {
        return actuals;
    }

    protected Symbol type_name;
    protected Symbol name;
    protected List<ExpressionNode> actuals;

    public StaticDispatchNode(int lineNumber,
                         ExpressionNode expr, Symbol type_name, Symbol name, List<ExpressionNode> actuals) {
        super(lineNumber);
        this.expr = expr;
        this.type_name = type_name;
        this.name = name;
        this.actuals = actuals;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }

}
