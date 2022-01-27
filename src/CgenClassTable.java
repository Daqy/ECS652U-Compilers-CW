import ast.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for representing the inheritance tree during code
 * generation. You will need to fill in some of its methods and
 * potentially extend it in other useful ways.
 */
class CgenClassTable extends  HashMap<Symbol,CgenNode> {

    private void installBasicClasses() {
        Symbol filename
                = StringTable.stringtable.addString("<basic class>");


        LinkedList<FormalNode> formals;
        // A few special class names are installed in the lookup table
        // but not the class list.  Thus, these classes exist, but are
        // not part of the inheritance hierarchy.  No_class serves as
        // the parent of Object and the other special classes.
        // SELF_TYPE is the self class; it cannot be redefined or
        // inherited.  prim_slot is a class known to the code generator.

        put(TreeConstants.No_class,
                new CgenNode(new ClassNode(0,
                        TreeConstants.No_class,
                        TreeConstants.No_class,
                        filename),
                        true));

        put(TreeConstants.SELF_TYPE,
                new CgenNode(new ClassNode(0,
                        TreeConstants.SELF_TYPE,
                        TreeConstants.No_class,
                        filename),
                        true));

        put(TreeConstants.prim_slot,
                new CgenNode(new ClassNode(0,
                        TreeConstants.prim_slot,
                        TreeConstants.No_class,
                        filename),
                        false));

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
                new LinkedList<>(),
                TreeConstants.Object_,
                new NoExpressionNode(0)));

        Object_class.add(new MethodNode(0,
                TreeConstants.type_name,
                new LinkedList<>(),
                TreeConstants.Str,
                new NoExpressionNode(0)));

        Object_class.add(new MethodNode(0,
                TreeConstants.copy,
                new LinkedList<>(),
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));

        installClass(new CgenNode(Object_class, true));

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

        formals = new LinkedList<>();
        formals.add(
                new FormalNode(0,
                        TreeConstants.arg,
                        TreeConstants.Str));

        IO_class.add(new MethodNode(0,
                TreeConstants.out_string,
                formals,
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));


        formals = new LinkedList<>();
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
                new LinkedList<>(),
                TreeConstants.Str,
                new NoExpressionNode(0)));

        IO_class.add(new MethodNode(0,
                TreeConstants.in_int,
                new LinkedList<>(),
                TreeConstants.Int,
                new NoExpressionNode(0)));


        installClass(new CgenNode(IO_class, true));

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

        installClass(new CgenNode(Int_class, true));

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

        installClass(new CgenNode(Bool_class, true));

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
                new LinkedList<>(),
                TreeConstants.Int,
                new NoExpressionNode(0)));

        formals = new LinkedList<>();
        formals.add(new FormalNode(0,
                TreeConstants.arg,
                TreeConstants.Str));
        Str_class.add(new MethodNode(0,
                TreeConstants.concat,
                formals,
                TreeConstants.Str,
                new NoExpressionNode(0)));

        formals = new LinkedList<>();
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

        installClass(new CgenNode(Str_class, true));
    }

    // The following creates an inheritance graph from
    // a list of classes.  The graph is implemented as
    // a tree of `CgenNode', and class names are placed
    // in the base class symbol table.

    private void installClass(CgenNode nd) {
        if (putIfAbsent(nd.getClassNode().getName(), nd) != null)
            throw new RuntimeException("Malformed class hierarchy");
    }

    private void installClasses(List<ClassNode> cs) {
        for (ClassNode c : cs) {
            installClass(new CgenNode(c, false));
        }
    }

    private void buildInheritanceTree() {
        for (CgenNode e : values()) {
            setRelations(e);
        }
    }

    private void setRelations(CgenNode nd) {
        CgenNode parent = get(nd.getClassNode().getParent());
        nd.setParentNd(parent);
        parent.addChild(nd);
    }

    /**
     * Constructs a new class table and inheritance graph
     */
    public CgenClassTable(List<ClassNode> cls) {

        if (Flags.cgen_debug) System.out.println("Building CgenClassTable");

        installBasicClasses();
        installClasses(cls);
        buildInheritanceTree();
    }

    /**
     * Gets the root of the inheritance tree
     */
    public CgenNode root() {
        return get(TreeConstants.Object_);
    }
}

    
