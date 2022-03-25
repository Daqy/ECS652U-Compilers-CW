import ast.visitor.BaseVisitor;
import java.util.HashMap;
import ast.*;
public class ScopeCheckingVisitor extends BaseVisitor<Object, Object> {
    public ScopeCheckingVisitor() {
        /*
        SymbtabExample.java gives an example of how to do this maybe?
        maybe we enter scope, check, and exit etc.
        */
    }
    private HashMap<Symbol, ClassNode> classMap = new HashMap<Symbol, ClassNode>();
    private Symbol currentClassNodeName;
    private Symbol filename;

    public Object visit(ProgramNode _node, Object data) {
        Semant.symtable.enterScope();
        for (ClassNode node : _node.getClasses()) {
            node.accept(this, data); 
        }
        if(Semant.symtable.lookup(TreeConstants.Main) == null) {
            Utilities.semantError(filename, _node).println("Class Main is not defined");
        }
        return null;
    };
    public Object visit(ClassNode _node, Object data) {
        filename = _node.getFilename();
        classMap.put(_node.getName(), _node);
        currentClassNodeName = _node.getName();
        for (FeatureNode node : _node.getFeatures()) {
            node.accept(this, data);
        }
        Semant.symtable.enterScope();

        if((Semant.symtable.lookup(_node.getName()) != null)){
            Utilities.semantError(filename, _node).println("Class " + _node.getName() + " was previously defined.");
        } else {
            Semant.symtable.addId(_node.getName(), new HashMap<Symbol, ClassNode>() {{put(_node.getParent(), _node);}});
        }
        return null;
    }
    public Object visit(MethodNode _node, Object data) {
        Semant.symtable.enterScope();
        Semant.symtable.addId(TreeConstants.InMethodNode_, _node);
        boolean added = false;
        if (Semant.symtable.lookup(_node.getName()) == null) {
            Semant.symtable.addId(_node.getName(), new HashMap<Symbol, ClassNode>() {{put(_node.getReturn_type(), classMap.get(currentClassNodeName));}});
            added = true;
        } else {
            ClassNode methodNameExistInScope = (ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0];
            if (classMap.get(currentClassNodeName).getParent() == methodNameExistInScope.getName()){
                for (FeatureNode fnode : methodNameExistInScope.getFeatures()){
                    if (fnode instanceof MethodNode) {
                        MethodNode mnode = (MethodNode) fnode;
                        if (mnode.getName() == _node.getName()) {
                            if (mnode.getFormals().size() != _node.getFormals().size()) {
                                Utilities.semantError(filename, _node).println("Incompatible number of formal parameters in redefined method "+ _node.getName());
                            }
                            for (int index = 0; index < mnode.getFormals().size(); index++) {
                                if (mnode.getFormals().get(index).getType_decl() != _node.getFormals().get(index).getType_decl()) {
                                    Utilities.semantError(filename, _node).println("In redfined method "+ _node.getName()+ ", parameter type "+ _node.getFormals().get(index).getType_decl() + " is different from original type " + mnode.getFormals().get(index).getType_decl());
                                }
                            }
                            _node.getExpr().accept(this, data);
                            return null;
                        }
                    }
                }
            }
        }
        for (FormalNode node : _node.getFormals()) {
            node.accept(this, data);
            Semant.symtable.addId(_node.getName(), new HashMap<Symbol, ClassNode>() {{put(_node.getReturn_type(), classMap.get(currentClassNodeName));}});
        }
        if (!added) {
            Semant.symtable.addId(_node.getName(), new HashMap<Symbol, ClassNode>() {{put(_node.getReturn_type(), classMap.get(currentClassNodeName));}});
        }
        _node.getExpr().accept(this, data);
        return null;
    }
    public Object visit(AttributeNode _node, Object data) {
        _node.getInit().accept(this, data);
        if(_node.getName() == TreeConstants.self){
            Utilities.semantError(filename, _node).println("'self' cannot be the name of an attribute.");
        } else if (Semant.symtable.probe(_node.getName()) != null) {
            Utilities.semantError(filename, _node).println("Attribute " + _node.getName() + " has been defined in this scope.");
        } else if (Semant.symtable.lookup(_node.getName()) != null && classMap.get(currentClassNodeName).getParent() == ((ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0]).getName()) {
            Utilities.semantError(filename, _node).println("Attribute " + _node.getName() + " is an attribute of an inherited class.");
        } else {
            Semant.symtable.addId(_node.getName(), new HashMap<Symbol, ClassNode>() {{put(_node.getType_decl(), classMap.get(currentClassNodeName));}});
        }
        
        return null;
    };
    
    public Object visit(FormalNode _node, Object data) {
        if((Semant.symtable.probe(_node.getName()) != null)){
            Utilities.semantError(filename, _node).println("Formal parameter " + _node.getName() + " is multiply defined.");
        }
        Semant.symtable.addId(_node.getName(), new HashMap<Symbol, ClassNode>() {{put(_node.getType_decl(), classMap.get(currentClassNodeName));}});
        return null;
    }

    public Object visit(ObjectNode _node, Object data) {
        if (Semant.symtable.lookup(_node.getName()) != null) {
            if (currentClassNodeName != ((ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0]).getName()) {
                if(classMap.get(currentClassNodeName).getParent() != ((ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0]).getName()) {
                    Utilities.semantError(filename, _node).println("Undeclared identifier " + _node.getName() + ".");
                }
            }
        } else {
            if (_node.getName() != TreeConstants.self) {
                if (Semant.symtable.probe(TreeConstants.InMethodNode_) != null) {
                    Utilities.semantError(filename, _node).println("Undeclared identifier " + _node.getName() + ".");
                }
            }
        }
        return null;
    }

    public Object visit(LetNode _node, Object data) {
        Semant.symtable.enterScope();

        if (_node.getIdentifier() != TreeConstants.self) {
            Semant.symtable.addId(_node.getIdentifier(), new HashMap<Symbol, ClassNode>() {{put(_node.getType_decl(), classMap.get(currentClassNodeName));}});
        } else {
            Utilities.semantError(filename, _node).println("'self' cannot be bound in a 'let' expression.");
        }
        _node.getInit().accept(this, data);
        _node.getBody().accept(this, data);
        _node.setType(_node.getBody().getType());
        Semant.symtable.exitScope();
        return null;
    }

    public Object visit(BranchNode _node, Object data) {
        if (Semant.symtable.probe(_node.getType_decl()) == null && _node.getType_decl() != null) {
            Semant.symtable.addId(_node.getType_decl(), new HashMap<Symbol, ClassNode>() {{put(_node.getType_decl(), classMap.get(currentClassNodeName));}});
        } else {
            Utilities.semantError(filename, _node).println("Duplicate branch " + _node.getType_decl() + " in case statement.");
        }
        _node.getExpr().accept(this, data);
        return null;
    }

    public Object visit(LoopNode _node, Object data) {
        Semant.symtable.enterScope();
        _node.getCond().accept(this, data);
        _node.getBody().accept(this, data);
        return null;
    }

    public Object visit(CaseNode _node, Object data) {
        Semant.symtable.enterScope();
        _node.getExpr().accept(this, data);
        for (BranchNode node : _node.getCases()) {
            node.accept(this, data);
        }
        Semant.symtable.exitScope();
        return null;
    }
    public Object visit(BlockNode _node, Object data) {
         Semant.symtable.enterScope();
        for (ExpressionNode node : _node.getExprs()) {
            node.accept(this, data);
        }
        Semant.symtable.exitScope();
        return null;
    }
}
