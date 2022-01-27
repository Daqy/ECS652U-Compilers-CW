package ast;

import ast.visitor.TreeVisitor;

import java.util.LinkedList;
import java.util.List;

public class ProgramNode extends TreeNode {
    protected List<ClassNode> classes = new LinkedList<ClassNode>();


    public List<ClassNode> getClasses() {
        return classes;
    }

    public void add(ClassNode c) {classes.add(c);}

    public ProgramNode(int lineNumber) {
        super(lineNumber);
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);

    }

}
