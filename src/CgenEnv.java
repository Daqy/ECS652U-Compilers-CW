import ast.*;

import java.util.ArrayList;

public class CgenEnv {

    private Symbol classname;           // name of the corresponding class
    private Symbol filename;            // used in generating run time error messages

    private int classTag = -1;            // class tag of the class
    private int maxChildTag = -1;         // largest class tag of any child, used in code generation for case expr
    // Because classes are numbered depth first, class A is a subclass of B if A's tag lies
    // in the range B's tag <= A's tag <= (max descendant of B)'s tag

    public static int getLastTag() {
        return lastTag;
    }

    private static int lastTag = 0;    // counter for creating unique class tags

    /* Mapping from class tags to class names */
    static ArrayList<Symbol> classTags = new ArrayList<>();

    public void setClassTag() {
        assert (classTag == -1);
        assert(maxChildTag == -1);
        classTag = lastTag;
        lastTag = lastTag + 1;

        assert(classTags.size() == classTag);
        classTags.add(StringTable.stringtable.addString(classname.getName()));
    }

    public void setMaxChildTag() {
        assert (classTag > -1);
        assert (maxChildTag == -1);
        maxChildTag = lastTag-1;
    }

    // number of methods, including inherited methods
    private int numMethods;

    // maps method name to method information
    CgenSymbolTable<Symbol, Cgen.MethodInfo> methods;

    // maps method offsets to method information
    // this is used only to emit dispatch tables
    CgenSymbolTable<Integer, Cgen.MethodInfo> methodOffsets;

    public void addMethod(MethodNode node) {

        Symbol name = node.getName();
        int offset;
        // if method already defined in a superclass, use the same offset
        Cgen.MethodInfo minfo = methods.lookup(name);
        if (minfo != null) {
            offset = minfo.getOffset();
        } else {
            offset = numMethods++;
        }

        // create new method info object for this method
        CgenCalcTemps calcTemps = new CgenCalcTemps();
        node.getExpr().accept(calcTemps, null);
        minfo = new Cgen.MethodInfo(classname, name, offset, calcTemps.get());
        // add it to the env in the current scope, keep both symbol tables in sync
        methods.addId(name,minfo);
        methodOffsets.addId(offset,minfo);
    }

    CgenSymbolTable<Symbol, Cgen.VarInfo> vars;
    CgenSymbolTable<Integer, Cgen.AttrInfo> attributeOffsets;

    public static int getFreshLabel() {
        return nextLabel++;
    }

    // labels for jumps in generated code;
    // unique across the entire program.
    private static int nextLabel = 0;

    // the offset of the first attribute directly declared in this class
    // computed from the last offset of the parent class
    private int firstAttributeOffset;
    // number of attributes directly declared in this class
    private int numAttributes = 0;

    // number of temporaries required for initialization of all the attributes directly declared in this class
    private CgenCalcTemps calcTemps = new CgenCalcTemps();

    // Temporaries may be needed to evaluate the initialization
    // of attributes; the number of temporaries needed is the max
    // over the number of temps needed to calculate any attribute of the
    // class.
    public int getAttrTemps() {
        return calcTemps.get();
    }

    public void addAttribute(AttributeNode node) {

        // Assigns to this attribute an offset within an object.
        // Unlike methods, no need to check superclasses because attributes cannot be redefined.
        int offset = firstAttributeOffset + numAttributes++;
        Cgen.AttrInfo ainfo = new Cgen.AttrInfo(offset, node);
        node.accept(calcTemps, null);
        vars.addId(node.getName(), ainfo);
        attributeOffsets.addId(offset, ainfo);
    }

    public int getNextTempOffset() {
        return nextTempOffset;
    }

    private int nextTempOffset = 0; // next available temporary location

    //  Add a local variable.
    //  A negative location is a register, a positive location is a stack slot.
    public void addLocal(Symbol sym) {
        if (Flags.cgen_debug) System.err.println("    Adding local binding "+ sym + " " + nextTempOffset);
        vars.enterScope();
        vars.addId(sym,new Cgen.LocalInfo(nextTempOffset));
        nextTempOffset++;
    }

    // Remove a local variable, freeing up its slot in the stack frame.
    public void removeLocal() {
      if (Flags.cgen_debug) System.err.println("   Removing local binding.");
      vars.exitScope();
      --nextTempOffset;
    }

    CgenEnv(Symbol c, Symbol f, CgenEnv parent) {
        this.classname = c;
        this.filename = f;
        if (parent == null) {
            this.numMethods = 0;
            this.methods = new CgenSymbolTable<>();
            this.methodOffsets = new CgenSymbolTable<>();
            this.vars = new CgenSymbolTable<>();
            this.attributeOffsets = new CgenSymbolTable<>();
            this.firstAttributeOffset = 0;
        } else {
            this.numMethods = parent.numMethods;
            this.methods = new CgenSymbolTable<>(parent.methods);
            this.methodOffsets = new CgenSymbolTable<>(parent.methodOffsets);
            this.vars = new CgenSymbolTable<>(parent.vars);
            this.attributeOffsets = new CgenSymbolTable<>(parent.attributeOffsets);
            this.firstAttributeOffset = parent.firstAttributeOffset+parent.numAttributes;
        }
    }

    public Symbol getClassname() {
        return classname;
    }

    public Symbol getFilename() {
        return filename;
    }

    public int getFirstAttributeOffset() {
        return firstAttributeOffset;
    }

    public int getNumAttributes() {
        return numAttributes;
    }

    public int getClassTag() {
        return classTag;
    }

    public int getMaxChildTag() {
        return maxChildTag;
    }

    public int getNumMethods() {
        return numMethods;
    }
}
