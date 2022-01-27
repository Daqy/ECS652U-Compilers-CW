import ast.AttributeNode;
import ast.MethodNode;
import ast.Symbol;

public class CgenOffsetsVisitor extends CgenVisitor<Void, CgenEnv> {
    /*
       Traverse inheritance tree in DFS order and assign tags
       class last_tag is pre index of the node
       max_child is post index of the node
      */
    @Override
    Void visit(CgenNode v) {
        Symbol classname = v.getClassNode().getName();
        Symbol f = v.getClassNode().getFilename();

        if (Flags.cgen_debug) System.err.println("Building CgenNode for " + classname);

        CgenNode p = v.getParentNd();
        assert (p != null);
        v.env = new CgenEnv(classname, f, p.env);
        v.env.setClassTag();
        v.getClassNode().accept(this, v.env);
        super.visit(v);
        v.env.setMaxChildTag();
        return null;
    }

    @Override
    public Void visit(MethodNode node, CgenEnv env) {
        env.addMethod(node);
        return null;
    }

    @Override
    public Void visit(AttributeNode node, CgenEnv env) {
        env.addAttribute(node);
        return null;
    }
}
