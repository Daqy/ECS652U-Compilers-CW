package ast;

import ast.visitor.TreeVisitor;

public class NewNode extends ExpressionNode {
    public Symbol getType_name() {
        return type_name;
    }

    protected Symbol type_name;

    public NewNode(int lineNumber, Symbol name) {
        super(lineNumber);
        this.type_name = name;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
