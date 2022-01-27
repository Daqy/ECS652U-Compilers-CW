package ast;

import ast.visitor.TreeVisitor;

import java.util.List;

public class MethodNode  extends FeatureNode {
    protected Symbol name;
    protected List<FormalNode> formals;
    protected Symbol return_type;
    protected ExpressionNode expr;

    public Symbol getName() {
        return name;
    }

    public List<FormalNode> getFormals() {
        return formals;
    }

    public Symbol getReturn_type() {
        return return_type;
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public MethodNode(int lineNumber, Symbol name, List<FormalNode> formals, Symbol return_type, ExpressionNode expr) {
        super(lineNumber);
        this.name = name;
        this.formals = formals;
        this.return_type = return_type;
        this.expr = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
