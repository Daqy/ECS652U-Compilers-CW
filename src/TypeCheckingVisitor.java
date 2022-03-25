import ast.visitor.BaseVisitor;
import ast.*;

import java.util.HashMap;

public class TypeCheckingVisitor extends BaseVisitor<Object, Object> {
    /* Perform type checking here, assume we will have to check for every type,
    and maybe including types within functions too.. i.e adding two things together
    // and making sure they're both type int etc? */
    private boolean inCaseNode = false;
    private Symbol filename;
    private HashMap<Symbol, TreeNode> classMap = new HashMap<Symbol, TreeNode>();
    private Symbol currentClassNodeName;
    
    
    public Object visit(ProgramNode _node, Object data) {
        for (ClassNode classNode : _node.getClasses()) {
            classNode.accept(this, data);
        }
        return null;
    }; // Done

    public Object visit(ClassNode _node, Object data) {
        filename = _node.getFilename();
        classMap.put(_node.getName(), _node);
        currentClassNodeName = _node.getName();
        for (FeatureNode featureNode : _node.getFeatures()) {
            featureNode.accept(this, data);
        }
        return null;
    }; // Done

    public Object visit(MethodNode _node, Object data) {
        for (FormalNode formalNode: _node.getFormals()){
            formalNode.accept(this,data);
            Semant.symtable.addId(formalNode.getName(), formalNode.getType_decl());
        }
        _node.getExpr().accept(this, data);

        if (Semant.symtable.lookup(_node.getReturn_type()) == null && _node.getReturn_type() != TreeConstants.SELF_TYPE) {
            Utilities.semantError(filename, _node).println(
                "Undefined return type " + _node.getReturn_type() + " in method " + _node.getName() + "."
            );
        } else if (Semant.symtable.lookup(_node.getName()) != null && _node.getExpr().getType() != _node.getReturn_type() && _node.getReturn_type() != TreeConstants.Object_) {
            if (_node.getExpr().getType() == TreeConstants.SELF_TYPE) {
                if (currentClassNodeName != _node.getReturn_type() && ((ClassNode) classMap.get(currentClassNodeName)).getParent() != _node.getReturn_type()) {
                    Utilities.semantError(filename, _node).println(
                        "Inferred return type " + _node.getExpr().getType() + " of method " + _node.getName() + 
                        " does not conform to declared return type " + _node.getReturn_type() + "."
                    );
                }
            } else if (_node.getExpr().getType() != null && _node.getExpr().getType() != TreeConstants.Object_) {
                Utilities.semantError(filename, _node).println(
                    "Inferred return type " + ((_node.getExpr().getType() == TreeConstants.No_type) ? TreeConstants.Object_ : _node.getExpr().getType()) + " of method " + _node.getName() + 
                    " does not conform to declared return type " + _node.getReturn_type() + "."
                );
            };
            
        }
        return null; 
    }
    
    public Object visit(AttributeNode _node, Object data) {
        _node.getInit().accept(this,data);

        if (_node.getType_decl() != null) {
            Semant.symtable.addId(_node.getName(), _node);
        }
        return null;
    }

    public Object visit(FormalNode _node, Object data) {
        if(_node.getName() == TreeConstants.self || _node.getType_decl() == TreeConstants.SELF_TYPE) {
            Utilities.semantError(filename, _node).println(
                (_node.getName() == TreeConstants.self) ? "'self' cannnot be the name of a formal parameter." :
                "Formal parameter " + _node.getName() + " cannot have type "+ _node.getType_decl() + ".");
        }
        return null;
    }
    
    public Object visit(BoolBinopNode _node, Object data){
        _node.getE1().accept(this,data);
        _node.getE2().accept(this,data);
        Symbol E1type = _node.getE1().getType();
        if (E1type != _node.getE2().getType() && (E1type == TreeConstants.Str || E1type == TreeConstants.Bool || E1type == TreeConstants.Int)) {
            Utilities.semantError(filename, _node).println("Illegal comparison with a basic type.");
        }
        
        _node.setType(TreeConstants.Bool);
        return null;
    }

    public Object visit(StringConstNode _node, Object data) {
        _node.setType(TreeConstants.Str);
        return null;
    }

    public Object visit(IntBinopNode _node, Object data) {
        _node.getE1().accept(this,data);
        _node.getE2().accept(this,data);
        if (_node.getE1().getType() != TreeConstants.Int || _node.getE2().getType() != _node.getE1().getType()) {
            Utilities.semantError(filename, _node).println(
                "non-Int arguments: " + _node.getE1().getType() + " + " + _node.getE2().getType()
            );
        } else {
            _node.setType(TreeConstants.Int);
        }
        return null;
    }
    
    public Object visit(IntUnopNode _node, Object data) {
        if (_node instanceof NegNode) {
            _node.getE1().accept(this, data);
        }
        _node.setType(TreeConstants.Int);
        return null;
    }
    public Object visit(NewNode _node, Object data) {
        if (Semant.symtable.lookup(_node.getType_name()) == null && _node.getType_name() != TreeConstants.SELF_TYPE) {
            Utilities.semantError(filename, _node).println("'new' used with undefined class " + _node.getType_name());
        }
        _node.setType(_node.getType_name());
        return null;
    }
    public Object visit(IntConstNode _node, Object data) {
        _node.setType(TreeConstants.Int); 
        return null;
    }

    public Object visit(IsVoidNode _node, Object data) {
        _node.getE1().accept(this, data);
        _node.setType(TreeConstants.Bool);
        return null;
    }
    public Object visit(NoExpressionNode _node, Object data) {
        _node.setType(TreeConstants.No_type);
        return null;
    }

    public Object visit(AssignNode _node, Object data) {
        if (_node.getName() == TreeConstants.self) Utilities.semantError(filename, _node).println("Cannot assign to 'self'.");
        _node.getExpr().accept(this, data);

        if (Semant.symtable.lookup(_node.getName()) != null) {
            _node.setType(_node.getExpr().getType());

            if (Semant.symtable.lookup(_node.getName()) instanceof Symbol) {
                if (Semant.symtable.lookup(_node.getName()) != _node.getExpr().getType()) Utilities.semantError(filename, _node).println(
                    "Type " + _node.getExpr().getType() + " of assigned expression does not conform to declared type " + Semant.symtable.lookup(_node.getName()) + 
                    " of identifier " + _node.getName() +"."
                );
            } else {
                if (Semant.symtable.lookup(_node.getName()) instanceof LetNode) {
                    if (((LetNode) Semant.symtable.lookup(_node.getName())).getType_decl() != _node.getExpr().getType()) Utilities.semantError(filename, _node).println(
                        "Type " + _node.getExpr().getType() + " of assigned expression does not conform to declared type " + ((LetNode) Semant.symtable.lookup(_node.getName())).getType_decl() + 
                        " of identifier " + _node.getName() +"."
                    );
                } else {
                    AttributeNode attributeNode;
                    try {
                        attributeNode = (AttributeNode) Semant.symtable.lookup(_node.getName());
                        
                    } catch(Exception e) {
                        attributeNode = (AttributeNode) ((HashMap) Semant.symtable.lookup(_node.getName())).keySet().toArray()[0];
                    }
                    if (attributeNode.getType_decl() != _node.getExpr().getType()) Utilities.semantError(filename, _node).println(
                        "Type " + _node.getExpr().getType() + " of assigned expression does not conform to declared type " + attributeNode.getType_decl() + 
                        " of identifier " + _node.getName() +"."
                    );
                }
                
            }
        }
        return null;
    }

    public Object visit(BlockNode _node, Object data) {
        Semant.symtable.enterScope();
        for(ExpressionNode expressionNode: _node.getExprs()){
            expressionNode.accept(this, data);
        }
        _node.setType(_node.getExprs().get(_node.getExprs().size()-1).getType());
        Semant.symtable.exitScope();
        return null;
    }

    public Object visit(LetNode _node, Object data) {
        Semant.symtable.enterScope();
        if (_node.getIdentifier() != TreeConstants.self) {
            Semant.symtable.addId(_node.getIdentifier(), _node);
        }

        _node.getInit().accept(this, data);
        _node.getBody().accept(this, data);
        
        if(Semant.symtable.lookup(_node.getInit().getType()) != null) {
            Symbol nodeType = (Symbol) ((HashMap) Semant.symtable.lookup(_node.getInit().getType())).keySet().toArray()[0];
            if (nodeType != _node.getType_decl() && _node.getType_decl() != _node.getInit().getType()) {
                Utilities.semantError(filename, _node).println(
                    "Inferred type " +  _node.getInit().getType() + " of initialization of " + _node.getIdentifier() + 
                    " does not conform to identifier's declared type " + _node.getType_decl() + "." 
                );
            }
        }
        _node.setType(_node.getBody().getType());
        Semant.symtable.exitScope();
        return null;
    };

    public Object visit(CaseNode _node, Object data) {
        Semant.symtable.enterScope();
        inCaseNode = true;
        _node.getExpr().accept(this, data);

        for (BranchNode branchNode: _node.getCases()) {
            branchNode.accept(this, data);
        }

        Symbol type = _node.getCases().get(0).getExpr().getType(); 

        for (BranchNode branchNode: _node.getCases()) {
            if (branchNode.getExpr().getType() != _node.getCases().get(0).getExpr().getType()) {
                ClassNode originalBranchClass1 = (ClassNode) ((HashMap) Semant.symtable.lookup(_node.getCases().get(0).getExpr().getType())).values().toArray()[0];
                ClassNode originalBranchClass2 = (ClassNode) ((HashMap) Semant.symtable.lookup(branchNode.getExpr().getType())).values().toArray()[0];
                ClassNode branchClass1 = originalBranchClass1;
                ClassNode branchClass2 = originalBranchClass2;
                while (true) {
                    if (branchClass1.getName() == originalBranchClass2.getName()) {
                        type = branchClass1.getName();
                        break;
                    } else if (branchClass2.getName() == originalBranchClass1.getName()) {
                        type = branchClass2.getName();
                        break;
                    } else if (branchClass1.getName() == originalBranchClass2.getParent()) {
                        type = branchClass1.getName();
                        break;
                    } else if (branchClass2.getName() == originalBranchClass1.getParent()) {
                        type = branchClass2.getName();
                        break;
                    } else if (branchClass1.getParent() != TreeConstants.Object_ || branchClass2.getParent() != TreeConstants.Object_) {
                        branchClass1 = branchClass1.getParent() != TreeConstants.Object_ ? (ClassNode) ((HashMap) Semant.symtable.lookup(branchClass1.getParent())).values().toArray()[0] : branchClass1;
                        branchClass2 = branchClass2.getParent() != TreeConstants.Object_ ? (ClassNode) ((HashMap) Semant.symtable.lookup(branchClass2.getParent())).values().toArray()[0] : branchClass2;
                    } else {
                        type = TreeConstants.Object_;
                        break;
                    }
                }
            }
        }

        _node.setType(type);
        Semant.symtable.exitScope();
        inCaseNode = false;
        return null;
    }

    public Object visit(ObjectNode _node, Object data) {
        if (_node.getName() == TreeConstants.self) {
            _node.setType(TreeConstants.SELF_TYPE);
        } else if (Semant.symtable.probe(_node.getName()) != null) {
            if (Semant.symtable.probe(_node.getName()) instanceof Symbol) {
                _node.setType((Symbol) Semant.symtable.probe(_node.getName()));
            } else {
                TreeNode probedNode = (TreeNode) Semant.symtable.probe(_node.getName());
                if (probedNode instanceof LetNode) {
                    _node.setType((Symbol) ((LetNode) probedNode).getType_decl());
                } else if (probedNode instanceof AttributeNode) {
                    _node.setType((Symbol) ((AttributeNode) probedNode).getType_decl());
                } else {
                    _node.setType((Symbol) Semant.symtable.probe(_node.getName()));
                }
            }
        }
        else if (Semant.symtable.lookup(_node.getName()) != null) {
            // if (((ClassNode) classMap.get(currentClassNodeName)).getName() != Semant.symtable.lookup(_node.getName())) {
            //     if(((ClassNode) classMap.get(currentClassNodeName)).getParent().getName() != Semant.symtable.lookup(_node.getName())) {
                    
            //     }
            // }
            if (Semant.symtable.lookup(_node.getName()) instanceof AttributeNode) {
                _node.setType(((AttributeNode) Semant.symtable.lookup(_node.getName())).getType_decl());
            } else if (Semant.symtable.lookup(_node.getName()) instanceof LetNode) {
                _node.setType(((LetNode) Semant.symtable.lookup(_node.getName())).getType_decl());
            } else {
                _node.setType((Symbol) Semant.symtable.lookup(_node.getName()));
            }
        }
        else {
            if(!(inCaseNode)) {
                Utilities.semantError(filename, _node).println("Undeclared identifier " + _node.getName() + ".");
            }
        }
        return null;
    }
    
    public Object visit(CondNode _node, Object data) {
        Semant.symtable.enterScope();
        _node.getThenExpr().accept(this, data);
        _node.getElseExpr().accept(this, data);
        
        Symbol thenTypeSymbol = (_node.getThenExpr().getType() == TreeConstants.SELF_TYPE) ? ((ClassNode) classMap.get(currentClassNodeName)).getName() : _node.getThenExpr().getType();
        Symbol elseTypeSymbol = (_node.getElseExpr().getType() == TreeConstants.SELF_TYPE) ? ((ClassNode) classMap.get(currentClassNodeName)).getName() : _node.getElseExpr().getType();

        Symbol type = thenTypeSymbol;
        if (thenTypeSymbol == null && elseTypeSymbol == null) {
            type = TreeConstants.Object_;
        } else {
            int loopIteration = 0;
            while (true) {
                if (thenTypeSymbol != elseTypeSymbol) {
                    loopIteration += 1;
                    Symbol parentThenTypeSymbol = (Symbol) Semant.symtable.lookup(thenTypeSymbol);
                    Symbol parentElseTypeSymbol = (Symbol) Semant.symtable.lookup(elseTypeSymbol);
                    if (thenTypeSymbol == parentElseTypeSymbol) {
                        type = thenTypeSymbol;
                        break;
                    } else if (parentThenTypeSymbol == elseTypeSymbol) {
                        type = parentThenTypeSymbol;
                        break;
                    } else if (parentThenTypeSymbol == parentElseTypeSymbol) {
                        type = parentThenTypeSymbol;
                        break;
                    } else {
                        thenTypeSymbol = loopIteration%2 == 0 ? parentThenTypeSymbol : thenTypeSymbol;
                        elseTypeSymbol = loopIteration%2 == 0 ? elseTypeSymbol : parentElseTypeSymbol;
                    }
                } else {
                    break;
                }
            }
        }
        _node.setType(type);
        Semant.symtable.exitScope();
        return null;
    }

    public Object visit(BranchNode _node, Object data) {
        _node.getExpr().accept(this, data);
        Symbol classNodeParent = null;
        Symbol classNodeName = null;
        if (!(_node.getExpr() instanceof ObjectNode)) {
            ClassNode classnode = (ClassNode) ((HashMap) Semant.symtable.lookup(_node.getExpr().getType())).values().toArray()[0];
            classNodeParent = classnode.getParent();
            classNodeName = classnode.getName();
        }
        _node.getExpr().setType((classNodeParent != null && classNodeParent == _node.getType_decl()) ? classNodeName : _node.getType_decl());
        return null;
    }

    public Object visit(LoopNode _node, Object data) {
        _node.getBody().accept(this, data);
        _node.getCond().accept(this, data);
        if (_node.getCond().getType() != TreeConstants.Bool) {
            Utilities.semantError(filename, _node).println("Loop condition does not have type Bool.");
        }
        _node.setType(TreeConstants.Object_);
        return null;
    }

    public Object visit(BoolUnopNode _node, Object data) {
        _node.setType(TreeConstants.Bool);
        Semant.symtable.addId(_node.getType(), _node);
        return null;
    }

    public Object visit(BoolConstNode _node, Object data) {
        _node.setType(TreeConstants.Bool);
        return null;
    }

    public Object visit(DispatchNode _node, Object data) {
        _node.getExpr().accept(this, data);
        for (ExpressionNode expressionNode : _node.getActuals()) {
            expressionNode.accept(this, data);
        }

        Symbol type = _node.getExpr().getType();

        if (Semant.symtable.lookup(_node.getName()) != null) {
            if (((Symbol) ((HashMap) Semant.symtable.lookup(_node.getName())).keySet().toArray()[0]) == TreeConstants.SELF_TYPE) {
                _node.setType(type);
            } else if (_node.getExpr().getType() != TreeConstants.SELF_TYPE) {
                
                if (Semant.symtable.lookup(_node.getExpr().getType()) == null) {
                    Utilities.semantError(filename, _node).println("Dispatch to undefined method " + _node.getName() + ".");
                } else {
                    
                    Symbol getExprParentType = (Symbol) ((HashMap)Semant.symtable.lookup(_node.getExpr().getType())).keySet().toArray()[0];
                    ClassNode nodeParentClass = (ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0];
                    if (nodeParentClass.getName() != getExprParentType) {
                        
                        ClassNode superParentOfNodeClass = (ClassNode) ((HashMap) Semant.symtable.lookup( (nodeParentClass.getParent() == getExprParentType) ? _node.getExpr().getType() : nodeParentClass.getParent())).values().toArray()[0];
                        if (superParentOfNodeClass.getFeatures().size() == 0) {
                            
                            Utilities.semantError(filename, _node).println("Dispatch to undefined method " + _node.getName() + ".");
                        } else {
                            
                            boolean notInFeatures = true;
                            for (FeatureNode featureNode : superParentOfNodeClass.getFeatures()) {
                                if (featureNode instanceof MethodNode) {
                                    MethodNode _methodNode = (MethodNode) featureNode;
                                    if (_node.getName() == _methodNode.getName()) {
                                        notInFeatures = false;
                                        type = (getExprParentType == nodeParentClass.getParent()) ? _methodNode.getReturn_type() : type;
                                    }
                                }
                            }
                            if (notInFeatures) {
                                Utilities.semantError(filename, _node).println("Dispatch to undefined method " + _node.getName() + ".");
                            }
                        }
                    }
                    _node.setType(type);
                }
            }
            if (_node.getExpr().getType() == TreeConstants.SELF_TYPE) {
                _node.setType(((Symbol) ((HashMap) Semant.symtable.lookup(_node.getName())).keySet().toArray()[0]));
            }

            
            for (FeatureNode featureNode : ((ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0]).getFeatures()) {
                if (featureNode instanceof MethodNode) {
                    MethodNode methodNode = (MethodNode) featureNode;
                    int index = 0;
                    if (methodNode.getFormals().size() > 0 && _node.getName() == methodNode.getName()) {
                        for (ExpressionNode node : _node.getActuals()) {
                            if (node.getType() != methodNode.getFormals().get(index).getType_decl()) {
                                if (node instanceof DispatchNode) {
                                    DispatchNode tmp = (DispatchNode) node;
                                    if (Semant.symtable.lookup(tmp.getName()) != null) {
                                        Symbol getExprParentType = ((ClassNode) ((HashMap) Semant.symtable.lookup(tmp.getName())).values().toArray()[0]).getParent();
                                        if (getExprParentType != methodNode.getFormals().get(index).getType_decl()) {
                                            Utilities.semantError(filename, _node).println(
                                                "In call of method " + _node.getName() + ", type " + 
                                                getExprParentType + 
                                                " of parameter " + methodNode.getFormals().get(index).getName() + 
                                                " does not conform to declared type " + 
                                                methodNode.getFormals().get(index).getType_decl() +".");
                                        }
                                    }
                                } else {
                                    Symbol getExprParentType = ((ClassNode) ((HashMap) Semant.symtable.lookup((node.getType() == TreeConstants.SELF_TYPE ? methodNode.getFormals().get(index).getType_decl() : node.getType()))).values().toArray()[0]).getParent();
                                    if ((getExprParentType != methodNode.getFormals().get(index).getType_decl()) && node.getType() != TreeConstants.SELF_TYPE) {
                                        Utilities.semantError(filename, _node).println(
                                            "In call of method " + _node.getName() + " type " + 
                                            node.getType() + " of parameter " 
                                            + methodNode.getFormals().get(index).getName() + " does not conform to declared type " 
                                            + methodNode.getFormals().get(index).getType_decl() +"."
                                        );
                                    } else if ((getExprParentType != TreeConstants.Object_) && node.getType() == TreeConstants.SELF_TYPE) {
                                        Utilities.semantError(filename, _node).println(
                                            "In call of method " + _node.getName() + " type " + 
                                            node.getType()+ " of parameter " 
                                            + methodNode.getFormals().get(index).getName() + " does not conform to declared type " 
                                            + methodNode.getFormals().get(index).getType_decl() +"."
                                        );
                                    }
                                }
                            }
                            index++;
                        }
                    }
                }
            }
        }
        
        return null;
    }

    public Object visit(StaticDispatchNode _node, Object data) {
        _node.getExpr().accept(this, data);
        for (ExpressionNode expressionNode : _node.getActuals()) {
            expressionNode.accept(this, data);
        }
        
        Symbol type = (_node.getExpr().getType() == TreeConstants.SELF_TYPE) ? ((ClassNode) classMap.get(currentClassNodeName)).getName() : _node.getExpr().getType();
        ClassNode currentNodeClass = (ClassNode) ((HashMap) Semant.symtable.lookup(_node.getType_name())).values().toArray()[0];
        
        if (currentNodeClass == ((ClassNode) ((HashMap) Semant.symtable.lookup(type)).values().toArray()[0])) {
            if (currentNodeClass == ((ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0])) {
                _node.setType(currentNodeClass.getName());
            } else {
                Symbol mostLowerParentNode = null; 
                ClassNode originalBranchClass2 = (ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0];
                ClassNode branchClass1 = currentNodeClass;
                ClassNode branchClass2 = originalBranchClass2;
                while (true) {
                    if (branchClass1.getName() == originalBranchClass2.getName()) {
                        mostLowerParentNode = branchClass1.getName();
                        break;
                    } else if (branchClass2.getName() == currentNodeClass.getName()) {
                        mostLowerParentNode = branchClass2.getName();
                        break;
                    } else if (branchClass1.getName() == originalBranchClass2.getParent()) {
                        mostLowerParentNode = branchClass1.getName();
                        break;
                    } else if (branchClass2.getName() == currentNodeClass.getParent()) {
                        mostLowerParentNode = branchClass2.getName();
                        break;
                    } else if (branchClass1.getParent() != TreeConstants.Object_ || branchClass2.getParent() != TreeConstants.Object_) {
                        branchClass1 = branchClass1.getParent() != TreeConstants.Object_ ? (ClassNode) ((HashMap) Semant.symtable.lookup(branchClass1.getParent())).values().toArray()[0] : branchClass1;
                        branchClass2 = branchClass2.getParent() != TreeConstants.Object_ ? (ClassNode) ((HashMap) Semant.symtable.lookup(branchClass2.getParent())).values().toArray()[0] : branchClass2;
                    } else {
                        mostLowerParentNode = TreeConstants.Object_;
                        break;
                    }
                }
                if (mostLowerParentNode != null && mostLowerParentNode == originalBranchClass2.getName()) {
                    boolean notInFeatures = true;
                    for (FeatureNode featureNode : originalBranchClass2.getFeatures()) {
                        if (featureNode instanceof MethodNode) {
                            MethodNode methodNode = (MethodNode) featureNode;
                            if (methodNode.getName() == _node.getName()) {
                                notInFeatures = false;
                                _node.setType(methodNode.getReturn_type());
                            }
                        }
                    }
                    if (notInFeatures) {
                        Utilities.semantError(filename, _node).println("Dispatch to undefined method " + _node.getName() + ".");
                        _node.setType(TreeConstants.No_type);
                    }
                }
            }
        } else {
            Symbol mostLowerParentNode = null; 
            ClassNode originalBranchClass2 = (ClassNode) ((HashMap) Semant.symtable.lookup(type)).values().toArray()[0];
            ClassNode branchClass1 = currentNodeClass;
            ClassNode branchClass2 = originalBranchClass2;
            while (true) {
                if (branchClass1.getName() == originalBranchClass2.getName()) {
                    mostLowerParentNode = branchClass1.getName();
                    break;
                } else if (branchClass2.getName() == currentNodeClass.getName()) {
                    mostLowerParentNode = branchClass2.getName();
                    break;
                } else if (branchClass1.getName() == originalBranchClass2.getParent()) {
                    mostLowerParentNode = branchClass1.getName();
                    break;
                } else if (branchClass2.getName() == currentNodeClass.getParent()) {
                    mostLowerParentNode = branchClass2.getName();
                    break;
                } else if (branchClass1.getParent() != TreeConstants.Object_ || branchClass2.getParent() != TreeConstants.Object_) {
                    branchClass1 = branchClass1.getParent() != TreeConstants.Object_ ? (ClassNode) ((HashMap) Semant.symtable.lookup(branchClass1.getParent())).values().toArray()[0] : branchClass1;
                    branchClass2 = branchClass2.getParent() != TreeConstants.Object_ ? (ClassNode) ((HashMap) Semant.symtable.lookup(branchClass2.getParent())).values().toArray()[0] : branchClass2;
                } else {
                    mostLowerParentNode = TreeConstants.Object_;
                    break;
                }
            }
            if (mostLowerParentNode == TreeConstants.Object_) {
                Utilities.semantError(filename, _node).println(
                    "Expression Type " + _node.getExpr().getType() + " does not conform to declared static dispatch type " +
                    _node.getType_name() + "."
                );
                _node.setType(TreeConstants.No_type);
            } else {
                if (((ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0]).getParent() == mostLowerParentNode) {
                    Utilities.semantError(filename, _node).println(
                        "Expression Type " + _node.getExpr().getType() + " does not conform to declared static dispatch type " +
                        _node.getType_name() + "."
                    );
                }
                if (((ClassNode) classMap.get(currentClassNodeName)) == (ClassNode) ((HashMap) Semant.symtable.lookup(_node.getName())).values().toArray()[0]) {
                    Utilities.semantError(filename, _node).println(
                        "Expression Type " + _node.getExpr().getType() + " does not conform to declared static dispatch type " +
                        _node.getType_name() + "."
                    );
                    _node.setType(TreeConstants.No_type);
                } else {
                    boolean notInFeatures = true;
                    for (FeatureNode featureNode : currentNodeClass.getFeatures()) {
                        if (featureNode instanceof MethodNode) {
                            MethodNode methodNode = (MethodNode) featureNode;
                            if (methodNode.getName() == _node.getName()) {
                                notInFeatures = false;
                                _node.setType(methodNode.getReturn_type());
                            }
                        }
                    }
                    if (notInFeatures) {
                        Utilities.semantError(filename, _node).println("Dispatch to undefined method " + _node.getName() + ".");
                        _node.setType(TreeConstants.No_type);
                    }
                }
            }
        }
        return null;
    }
}
