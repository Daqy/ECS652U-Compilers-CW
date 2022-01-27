package ast;

import ast.visitor.TreeVisitor;

public class Symbol implements Tree {
    private String name;
    private int index;

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Symbol(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }

    public String toString() {
	return name;
    }
}
