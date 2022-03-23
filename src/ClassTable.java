import ast.*;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

/**
 * This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.
 */
class ClassTable {
    /**
     * Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     */
    private void installBasicClasses() {
        Symbol filename
                = StringTable.stringtable.addString("<basic class>");

        LinkedList<FormalNode> formals;

        // The following demonstrates how to create dummy parse trees to
        // refer to basic Cool classes.  There's no need for method
        // bodies -- these are already built into the runtime system.

        // IMPORTANT: The results of the following expressions are
        // stored in local variables.  You will want to do something
        // with those variables at the end of this method to make this
        // code meaningful.

        // The Object class has no parent class. Its methods are
        //        cool_abort() : Object    aborts the program
        //        type_name() : Str        returns a string representation
        //                                 of class name
        //        copy() : SELF_TYPE       returns a copy of the object

        ClassNode Object_class =
                new ClassNode(0,
                        TreeConstants.Object_,
                        TreeConstants.No_class,
                        filename);

        Object_class.add(new MethodNode(0,
                TreeConstants.cool_abort,
                new LinkedList<FormalNode>(),
                TreeConstants.Object_,
                new NoExpressionNode(0)));

        Object_class.add(new MethodNode(0,
                TreeConstants.type_name,
                new LinkedList<FormalNode>(),
                TreeConstants.Str,
                new NoExpressionNode(0)));

        Object_class.add(new MethodNode(0,
                TreeConstants.copy,
                new LinkedList<FormalNode>(),
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));

        // The IO class inherits from Object. Its methods are
        //        out_string(Str) : SELF_TYPE  writes a string to the output
        //        out_int(Int) : SELF_TYPE      "    an int    "  "     "
        //        in_string() : Str            reads a string from the input
        //        in_int() : Int                "   an int     "  "     "

        ClassNode IO_class =
                new ClassNode(0,
                        TreeConstants.IO,
                        TreeConstants.Object_,
                        filename);

        formals = new LinkedList<FormalNode>();
        formals.add(
                new FormalNode(0,
                        TreeConstants.arg,
                        TreeConstants.Str));

        IO_class.add(new MethodNode(0,
                TreeConstants.out_string,
                formals,
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));


        formals = new LinkedList<FormalNode>();
        formals.add(
                new FormalNode(0,
                        TreeConstants.arg,
                        TreeConstants.Int));
        IO_class.add(new MethodNode(0,
                TreeConstants.out_int,
                formals,
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));

        IO_class.add(new MethodNode(0,
                TreeConstants.in_string,
                new LinkedList<FormalNode>(),
                TreeConstants.Str,
                new NoExpressionNode(0)));

        IO_class.add(new MethodNode(0,
                TreeConstants.in_int,
                new LinkedList<FormalNode>(),
                TreeConstants.Int,
                new NoExpressionNode(0)));

        // The Int class has no methods and only a single attribute, the
        // "val" for the integer.

        ClassNode Int_class =
                new ClassNode(0,
                        TreeConstants.Int,
                        TreeConstants.Object_,
                        filename);

        Int_class.add(new AttributeNode(0,
                TreeConstants.val,
                TreeConstants.prim_slot,
                new NoExpressionNode(0)));

        // Bool also has only the "val" slot.
        ClassNode Bool_class =
                new ClassNode(0,
                        TreeConstants.Bool,
                        TreeConstants.Object_,
                        filename);

        Bool_class.add(new AttributeNode(0,
                TreeConstants.val,
                TreeConstants.prim_slot,
                new NoExpressionNode(0)));

        // The class Str has a number of slots and operations:
        //       val                              the length of the string
        //       str_field                        the string itself
        //       length() : Int                   returns length of the string
        //       concat(arg: Str) : Str           performs string concatenation
        //       substr(arg: Int, arg2: Int): Str substring selection

        ClassNode Str_class =
                new ClassNode(0,
                        TreeConstants.Str,
                        TreeConstants.Object_,
                        filename);
        Str_class.add(new AttributeNode(0,
                TreeConstants.val,
                TreeConstants.Int,
                new NoExpressionNode(0)));

        Str_class.add(new AttributeNode(0,
                TreeConstants.str_field,
                TreeConstants.prim_slot,
                new NoExpressionNode(0)));
        Str_class.add(new MethodNode(0,
                TreeConstants.length,
                new LinkedList<FormalNode>(),
                TreeConstants.Int,
                new NoExpressionNode(0)));

        formals = new LinkedList<FormalNode>();
        formals.add(new FormalNode(0,
                TreeConstants.arg,
                TreeConstants.Str));
        Str_class.add(new MethodNode(0,
                TreeConstants.concat,
                formals,
                TreeConstants.Str,
                new NoExpressionNode(0)));

        formals = new LinkedList<FormalNode>();
        formals.add(new FormalNode(0,
                TreeConstants.arg,
                TreeConstants.Int));
        formals.add(new FormalNode(0,
                TreeConstants.arg2,
                TreeConstants.Int));

        Str_class.add(new MethodNode(0,
                TreeConstants.substr,
                formals,
                TreeConstants.Str,
                new NoExpressionNode(0)));

	/* Do somethind with Object_class, IO_class, Int_class,
           Bool_class, and Str_class here */

    // We for loop, add into
    Semant.symtable.enterScope();

    Semant.symtable.addId(Object_class.getName(), new HashMap<Symbol, ClassNode>() {{put(TreeConstants.No_class, Object_class);}});
    for (FeatureNode _fnode : Object_class.getFeatures()) {
      if (_fnode instanceof MethodNode) {
        MethodNode _mnode = (MethodNode) _fnode;
        Semant.symtable.addId(_mnode.getName(), new HashMap<Symbol, ClassNode>() {{put(_mnode.getReturn_type(), Object_class);}});
      }
    }
    Semant.symtable.enterScope();

    ClassNode[] listOfClassNodes = {IO_class, Int_class, Bool_class, Str_class};

    for (int index = 0; index < listOfClassNodes.length; index++) {
      ClassNode _node = listOfClassNodes[index];
      Semant.symtable.addId(_node.getName(), new HashMap<Symbol, ClassNode>() {{put(_node.getParent(), _node);}});
      for (FeatureNode _fnode : _node.getFeatures()) {
        if(_fnode instanceof MethodNode){
          MethodNode _mnode = (MethodNode) _fnode;
          Semant.symtable.addId(_mnode.getName(), new HashMap<Symbol, ClassNode>() {{put(_mnode.getReturn_type(), _node);}});
        }
      }
    }
  }

  public ClassTable(List<ClassNode> cls) {
    /* for inheritance, check for redefinition, cannot inherit from class, and undefined class names,
      also check cool manual & slides for any other problems we need to look in for inheritance
    */
    installBasicClasses();
    Semant.symtable.enterScope();

    for (ClassNode _cnode : cls) {
      Semant.symtable.addId(_cnode.getName(), _cnode.getParent());
    }

    for (ClassNode _cnode : cls) {
      if (_cnode.getName() == TreeConstants.Object_ || _cnode.getName() == TreeConstants.Int || _cnode.getName() == TreeConstants.Str || _cnode.getName() == TreeConstants.SELF_TYPE || _cnode.getName() == TreeConstants.IO) {
        Utilities.semantError(_cnode.getFilename(), _cnode).println("Redefinition of basic class " + _cnode.getName() + ".");
      } else if (_cnode.getParent() == TreeConstants.Int || _cnode.getParent() == TreeConstants.Bool || _cnode.getParent() == TreeConstants.Str || _cnode.getParent() == TreeConstants.Main || _cnode.getParent() == TreeConstants.SELF_TYPE) {
        Utilities.semantError(_cnode.getFilename(), _cnode).println("Class " + _cnode.getName() + " cannot inherit class " + _cnode.getParent() + ".");
      } else if (Semant.symtable.lookup(_cnode.getParent()) == null) {
        Utilities.semantError(_cnode.getFilename(), _cnode).println("Class " + _cnode.getName() + " inherits from an undefined class " + _cnode.getParent() + ".");
      }
    }
    // System.out.println(Semant.symtable.toString());

  }
}

