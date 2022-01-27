package ast;

import ast.visitor.TreeVisitor;

public interface Tree {
    public <R,D> R accept(TreeVisitor<R,D> visitor, D data);
}
