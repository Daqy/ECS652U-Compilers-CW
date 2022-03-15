import ast.visitor.BaseVisitor;

public class TypeCheckingVisitor extends BaseVisitor<Object, Object> {
    /* Perform type checking here, assume we will have to check for every type,
    and maybe including types within functions too.. i.e adding two things together
    and making sure they're both type int etc? */
    public Object visit(ProgramNode _node, Object data) {

        for (ClassNode node : _node.getClasses()) {
            node.accept(this, data);
        }
        return null;
    };

    public Object visit(ClassNode _node, Object data) {
        System.out.println(_node.getFeatures());
        for (FeatureNode node : _node.getFeatures()) {
            node.accept(this, data);
        }
        return null;
    };

    public Object visit(FeatureNode _node, Object data) {
        if (_node instanceof MethodNode) {
            this.visit((MethodNode) _node, data);
        }
        if (_node instanceof AttributeNode) {
            this.visit((AttributeNode) _node, data);
        }
        return null;
    };
}