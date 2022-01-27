package ast;

import ast.visitor.TreeVisitor;

public class AttributeNode extends FeatureNode {
    public Symbol getName() {
        return name;
    }

    public Symbol getType_decl() {
        return type_decl;
    }

    public ExpressionNode getInit() {
        return init;
    }

    protected Symbol name;
    protected Symbol type_decl;
    protected ExpressionNode init;


    public AttributeNode(int lineNumber, Symbol name, Symbol type, ExpressionNode init) {
        super(lineNumber);
        this.name = name;
        this.type_decl = type;
        this.init = init;
    }
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
