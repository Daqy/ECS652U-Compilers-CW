import ast.AssignNode;
import ast.AttributeNode;
import ast.ExpressionNode;
import ast.NoExpressionNode;

class CgenEmitConstructors extends CgenEmitVisitor {
    @Override
    Void visit(CgenNode v) {
        env = v.env;
        if (Flags.cgen_debug) System.err.println("Coding init method of class " + env.getClassname() +
                " first attribute: " + env.getFirstAttributeOffset() +
                " # attributes: " + env.getNumAttributes() );

        Cgen.emitter.emitInitRef(env.getClassname());
        Cgen.emitter.emitLabel();

        int num_temps = v.env.getAttrTemps();
        if (Flags.cgen_debug) System.err.println("Number of temporaries = " + num_temps);

        env.vars.enterScope();
        // no formals
        env.vars.addId(TreeConstants.self, new Cgen.SelfInfo());

        Cgen.emitter.prologue(num_temps);

        // constructor of the parent class
        // must be invoked prior to initializing any of the attributes
        // of the class.
        // root does not have a parent
        if (v != Cgen.classTable.root()) {
            Cgen.emitter.emitInit(v.getClassNode().getParent());
        }

        for(int i = env.getFirstAttributeOffset(); i < env.getFirstAttributeOffset() + env.getNumAttributes(); i++){
            visit(v.env.attributeOffsets.lookup(i).getNode(), CgenConstants.ACC);
        }

        // result of evaluting a constructor is self
        Cgen.emitter.emitMove(CgenConstants.ACC,CgenConstants.SELF);

        Cgen.emitter.epilogue(num_temps,0);
        env.vars.exitScope();

        super.visit(v);
        return null;
    }

    @Override
    public String visit(AttributeNode node, String target) {
        ExpressionNode init = node.getInit();
        // check whether attribute has user-defined init expression
        if (init instanceof NoExpressionNode) return null;
        AssignNode an = new AssignNode(node.getLineNumber(), node.getName(), node.getInit());
        an.accept(this, target);
        return null;
    }
}
