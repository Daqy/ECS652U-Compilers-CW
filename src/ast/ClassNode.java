package ast;

import ast.visitor.TreeVisitor;

import java.util.LinkedList;
import java.util.List;

public class ClassNode extends TreeNode {
    protected Symbol name;
    protected Symbol parent;
    protected List<FeatureNode> features = new LinkedList<FeatureNode>();
    protected Symbol filename;

    public ClassNode(int lineNumber, Symbol name, Symbol parent, Symbol filename) {
        super(lineNumber);
        this.name = name;
        this.parent = parent;
        this.filename = filename;
    }

    public Symbol getName() {
        return name;
    }

    public Symbol getParent() {
        return parent;
    }

    public List<FeatureNode> getFeatures() {
        return features;
    }

    public Symbol getFilename() {
        return filename;
    }

    public void add(FeatureNode f) {features.add(f);}

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
