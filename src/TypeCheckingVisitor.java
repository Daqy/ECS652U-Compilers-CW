import ast.visitor.BaseVisitor;
import ast.*;

import java.util.HashMap;

public class TypeCheckingVisitor extends BaseVisitor<Object, Object> {
    /* Perform type checking here, assume we will have to check for every type,
    and maybe including types within functions too.. i.e adding two things together
    // and making sure they're both type int etc? */
    private Symbol filename;
    private HashMap<Symbol, ClassNode> classMap = new HashMap<Symbol, ClassNode>();
    private Symbol currentClassNodeName;
    
    
    public Object visit(ProgramNode _node, Object data) {
        Semant.symtable.enterScope();
        for (ClassNode node : _node.getClasses()) {
            node.accept(this, data);
        }
        Semant.symtable.exitScope();
        return null;
    };

    public Object visit(ClassNode _node, Object data) {
        filename = _node.getFilename();
        classMap.put(_node.getName(), _node);
        currentClassNodeName = _node.getName();
        for (FeatureNode node : _node.getFeatures()) {
            node.accept(this, data);
        }
        return null;
    };

    public Object visit(MethodNode _node, Object data) {
        Semant.symtable.enterScope();
        if (Semant.symtable.lookup(_node.getReturn_type()) == null && _node.getReturn_type() != TreeConstants.SELF_TYPE) {
            Utilities.semantError(filename, _node).println("Undefined return type " + _node.getReturn_type() + " in method " + _node.getName() + ".");
            for (FormalNode node : _node.getFormals()) {
                node.accept(this, data);
                Semant.symtable.addId(node.getName(), node.getType_decl());
            }
            Semant.symtable.addId(_node.getName(), _node);
            _node.getExpr().accept(this, data);
            Semant.symtable.exitScope();
            return null;
        }

        for (FormalNode node : _node.getFormals()) {
            node.accept(this, data);
            Semant.symtable.addId(node.getName(), node.getType_decl());
        }
        Semant.symtable.addId(_node.getName(), _node);
        _node.getExpr().accept(this, data);


        // System.out.println("---" + _node.getExpr().getType());
        // System.out.println("---" + _node.getExpr());
        if (_node.getExpr().getType() != _node.getReturn_type() && _node.getReturn_type() != TreeConstants.Object_) {
            Utilities.semantError(filename, _node).println("Inferred return type " + _node.getExpr().getType() + " of method " + _node.getName() + " does not conform to declared return type " + _node.getReturn_type() + ".");
        }
        Semant.symtable.exitScope();
        return null;
    }

    public Object visit(EqNode _node, Object data){
        _node.getE1().accept(this, data);
        _node.getE2().accept(this, data);

        if (_node.getE1().getType() != _node.getE2().getType()) {
            Utilities.semantError(filename, _node).println("Illegal comparison with a basic type.");
        }
        return null;
    }

    public Object visit(StringConstNode _node, Object data) {
        _node.setType(TreeConstants.Str);
        return null;
    }

    public Object visit(AttributeNode _node, Object data) {
        _node.getInit().accept(this, data);

        if (_node.getType_decl() != null) {
            Semant.symtable.addId(_node.getName(), new HashMap<Symbol, AttributeNode>() {{put(_node.getType_decl(), _node);}});
        }
        return null;
    }

    public Object visit(DispatchNode _node, Object data) {
        _node.getExpr().accept(this, data);
        for (ExpressionNode node : _node.getActuals()) {
            node.accept(this, data);
        }

        // System.out.println(Semant.symtable.toString());


        // System.out.println(_node.getActuals());
        // System.out.println("==========================");
        // System.out.println(_node);
        // System.out.println(_node.getExpr().getType());
        // System.out.println(((MethodNode) Semant.symtable.probe(_node.getName())).getReturn_type());
        if (Semant.symtable.probe(_node.getName()) != null) {
            for(FeatureNode fnode : classMap.get(currentClassNodeName).getFeatures()){
                if (fnode instanceof MethodNode){
                    MethodNode mnode = (MethodNode) fnode;
                    int index = 0;
                    if (mnode.getFormals().size() > 0) {
                        for(ExpressionNode node : _node.getActuals()) {
                            if (node instanceof DispatchNode){
                                DispatchNode tmp = (DispatchNode) node;
                                if (Semant.symtable.probe(tmp.getName()) != null) {
                                    if (((MethodNode) Semant.symtable.probe(tmp.getName())).getReturn_type() != mnode.getFormals().get(index).getType_decl()) {
                                        Utilities.semantError(filename, _node).println("In call of method " + _node.getName() + " type " + ((MethodNode) Semant.symtable.probe(tmp.getName())).getReturn_type() + " of parameter " + mnode.getFormals().get(index).getName() + " does not conform to declared type " + mnode.getFormals().get(index).getType_decl() +".");
                                    }
                                }
                            }

                            index++;
                        }
                    }
                }
                
            }


            if(((MethodNode) Semant.symtable.lookup(_node.getName())).getReturn_type() == TreeConstants.SELF_TYPE) {
                _node.setType(TreeConstants.SELF_TYPE);
            }
        }
        if (_node.getExpr().getType() == TreeConstants.SELF_TYPE) {
            _node.setType(TreeConstants.SELF_TYPE);
        } else {
            _node.setType(((MethodNode) Semant.symtable.lookup(_node.getName())).getReturn_type());
        }
        return null;
    } //condNode

    public Object visit(CondNode _node, Object data) {
        Semant.symtable.enterScope();
        _node.getThenExpr().accept(this, data);
        _node.getElseExpr().accept(this, data);

        Symbol thenTypeSymbol = _node.getThenExpr().getType();
        Symbol elseTypeSymbol = _node.getElseExpr().getType();

        Symbol type = thenTypeSymbol;
        Symbol loopType = null;
        if (thenTypeSymbol == null && elseTypeSymbol == null) {
            type = TreeConstants.Object_;
        } else {
            int loopIteration = 0;
            if (thenTypeSymbol != elseTypeSymbol) {
                while (loopType == null) {
                    loopIteration += 1;
                    Symbol parentThenTypeSymbol = (Symbol) Semant.symtable.lookup(thenTypeSymbol);
                    Symbol parentElseTypeSymbol = (Symbol) Semant.symtable.lookup(elseTypeSymbol);
                    if (thenTypeSymbol == parentElseTypeSymbol) {
                        loopType = thenTypeSymbol;
                    } else if (parentThenTypeSymbol == elseTypeSymbol) {
                        loopType = parentThenTypeSymbol;
                    } else if (parentThenTypeSymbol == parentElseTypeSymbol) {
                        loopType = parentThenTypeSymbol;
                    } else {
                        thenTypeSymbol = loopIteration%2 == 0 ? parentThenTypeSymbol : thenTypeSymbol;
                        elseTypeSymbol = loopIteration%2 == 0 ? elseTypeSymbol : parentElseTypeSymbol;
                    }
                }
            }
        }
        _node.setType(loopType == null ? type : loopType);
        Semant.symtable.exitScope();
        return null;
    }

    public Object visit(ObjectNode _node, Object data) {
        if (_node.getName() == TreeConstants.self) {
            _node.setType(TreeConstants.SELF_TYPE);
        } else if (Semant.symtable.probe(_node.getName()) != null) {
            _node.setType((Symbol) Semant.symtable.probe(_node.getName()));
        }
        else if (Semant.symtable.lookup(_node.getName()) != null) {
            _node.setType((Symbol) ((HashMap) Semant.symtable.lookup(_node.getName())).keySet().toArray()[0]);
        } else {
            Utilities.semantError(filename, _node).println("Undeclared identifier " + _node.getName() + ".");
        }
        return null;
    }

    public Object visit(AssignNode _node, Object data) {
        if (_node.getName() == TreeConstants.self) {
            Utilities.semantError(filename, _node).println("Cannot assign to 'self'.");
        }
        _node.getExpr().accept(this, data);


        if(Semant.symtable.lookup(_node.getName()) != null) {
            _node.setType(_node.getExpr().getType());
            Symbol nodeType = (Symbol) ((HashMap) Semant.symtable.lookup(_node.getName())).keySet().toArray()[0];
            if (nodeType != _node.getExpr().getType()) {
                Utilities.semantError(filename, _node).println("Type " + _node.getExpr().getType() + " of assigned expression does not conform to declared type " + nodeType + " of identifier " + _node.getName() +".");
            }
        }

        return null;
    }
    
    public Object visit(FormalNode _node, Object data) {
        if (_node.getName() == TreeConstants.self) {
            Utilities.semantError(filename, _node).println("'self' cannnot be the name of a formal parameter.");
        } else if (_node.getType_decl() == TreeConstants.SELF_TYPE) {
            Utilities.semantError(filename, _node).println("Formal parameter " + _node.getName() + " cannot have type "+ _node.getType_decl() + ".");
        }
        return null;
    }

    public Object visit(IntConstNode _node, Object data) {
        _node.setType(TreeConstants.Int);
        return null;
    }

    public Object visit(IntBinopNode _node, Object data) {
        if (!(_node.getE1().getType() == TreeConstants.Int && _node.getE1().getType() == _node.getE2().getType())) {
            Utilities.semantError(filename, _node).println("non-Int arguments:" + _node.getE1().getType() + " + " + _node.getE2().getType());
            return null;
        }
        _node.setType(TreeConstants.Int);
        return null;
    }
    public Object visit(IntUnopNode _node, Object data) {
        _node.setType(TreeConstants.Int);
        return null;
    }

    public Object visit(NewNode _node, Object data) {
        if (_node.getType_name() != TreeConstants.SELF_TYPE){
            Utilities.semantError(filename, _node).println("'new' used with undefined class " + _node.getType_name());
        }
        _node.setType(_node.getType_name());
        return null;
    }
    public Object visit(LetNode _node, Object data) {
        Semant.symtable.enterScope();
        if (_node.getIdentifier() != TreeConstants.self) {
            Semant.symtable.addId(_node.getIdentifier(), new HashMap<Symbol, LetNode>() {{put(_node.getType_decl(), _node);}});
        }

        _node.getInit().accept(this, data);
        _node.getBody().accept(this, data);

        if(Semant.symtable.lookup(_node.getInit().getType()) != null) {
            if (Semant.symtable.lookup(_node.getType_decl()) == _node.getInit().getType() && _node.getType_decl() != _node.getInit().getType()){
                Utilities.semantError(filename, _node).println("Inferred type " +  _node.getInit().getType() + " of initialization of " + _node.getIdentifier() + " does not conform to identifier's declared type " + _node.getType_decl() + "." );
            }
        }
        _node.setType(_node.getBody().getType());
        Semant.symtable.exitScope();
        return null;
    };
    // public Object visit(NoExpressionNode node, Object data){
    //     System.out.println("No Expr");
    //     _node.setType(TreeConstants.Int);
    //     return null;
    // }
}


// public Object visit(_node, Object data) {
//     return null;
// }