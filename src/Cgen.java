
import ast.*;

public class Cgen  {

    static CgenEmit emitter;
    /* inhertiance tree */
    static CgenClassTable classTable;
    /**
     * This method is the meat of the code generator.
     */
    public static void emit(ProgramNode program) {
        CgenVisitor cgenVisitor;
        classTable = new CgenClassTable(program.getClasses());

        // Compute class tags and various offsets
        CgenOffsetsVisitor offsetsVisitor = new CgenOffsetsVisitor();
        offsetsVisitor.visit(classTable.root());

        int st = classTable.get(TreeConstants.Str).env.getClassTag();
        int it = classTable.get(TreeConstants.Int).env.getClassTag();
        int bt = classTable.get(TreeConstants.Bool).env.getClassTag();
        emitter = new CgenEmit(st, it, bt);

        if (Flags.cgen_debug) System.err.println("coding global data");
        emitter.codeGlobalData();

        if (Flags.cgen_debug) System.err.println("choosing gc");
        emitter.codeSelectGc();

        if (Flags.cgen_debug) System.err.println("coding constants");
        emitter.codeConstants();

        if (Flags.cgen_debug) System.err.println("coding class table");
        emitter.codeClassTable();

        if (Flags.cgen_debug) System.err.println("coding object table");
        emitter.codeProtObjectTable();

        if (Flags.cgen_debug) System.err.println("coding dispatch tables");
        cgenVisitor = new CgenEmitDispatchTables();
        cgenVisitor.visit(classTable.root());

        if (Flags.cgen_debug) System.err.println("coding prototypes");
        cgenVisitor = new CgenEmitPrototypeObjects();
        cgenVisitor.visit(classTable.root());

        if (Flags.cgen_debug) System.out.println("coding global text");
        emitter.codeGlobalText();

        if (Flags.cgen_debug) System.err.println("coding init methods");
        cgenVisitor = new CgenEmitConstructors();
        cgenVisitor.visit(classTable.root());

        if (Flags.cgen_debug) System.err.println("coding methods");
        cgenVisitor = new CgenEmitMethods();
        cgenVisitor.visit(classTable.root());
    }

    public static class MethodInfo {
        // the name of this method
        Symbol methodname;
        // Represents method binding:
        // the name of the class where the method is defined
        // This information is needed for each method of a
        // class to define the class' dispatch table.
        Symbol classname;

        // offset in dispatch table
        int offset;

        // number of temporaries required by the method
        int temps;

        public MethodInfo(Symbol classname, Symbol name, int offset, int temps) {
            this.classname = classname;
            this.methodname = name;
            this.offset = offset;
            this.temps = temps;
        }

        public Symbol getMethodname() {
            return methodname;
        }

        public Symbol getClassname() {
            return classname;
        }

        public int getOffset() {
            return offset;
        }

        public int getTemps() {
            return temps;
        }
    }


    // For attributes. Offset from base address of an object.
    public static class AttrInfo extends VarInfo {

        AttributeNode node;

        public AttributeNode getNode() {
            return node;
        }

        public AttrInfo(int offset, AttributeNode node) {
            this.offset = offset;
            this.node = node;
        }

        @Override
        public String emitRef(String optionalDest) {
            /* TODO */
            return null;
        }

        @Override
        public void emitUpdate(String source) {
            /* TODO */
        }
    }

    // For local variables or formal parameters.
    // Offset from the frame pointer
    public static class LocalInfo extends VarInfo {
        public LocalInfo(int i) {
            offset = i;
        }

        @Override
        public String emitRef(String optionalDest) {
            String reg = CgenConstants.getRegister(offset);
            if (reg != null)
            {
                if (Flags.cgen_debug) System.err.println("     Local read from register "+ reg );
                return reg;
            } else {
                if (Flags.cgen_debug) System.err.println("     Local load from FP offset "+ offset );
                emitter.emitLoad(optionalDest, offset, CgenConstants.FP);
                return optionalDest;
            }
        }

        @Override
        public void emitUpdate(String source) {
            String reg = CgenConstants.getRegister(offset);
            if (reg != null)
            {
                if (Flags.cgen_debug) System.err.println("     Local store to register" + reg);
                emitter.emitMove(reg, source);
            }
            else
            {
                if (Flags.cgen_debug) System.err.println("     Local store to FP offset " + offset);
                emitter.emitStore(source, offset, CgenConstants.FP);
            }
        }
    }

    // For self object
    public static class SelfInfo extends VarInfo {
        public SelfInfo() {
            offset = 0;
        }

        @Override
        public String emitRef(String optionalDest) {
            emitter.emitMove(optionalDest, CgenConstants.SELF);
            return optionalDest;
        }
        @Override
        public void emitUpdate(String source) {
            Utilities.fatalError("Cannot assign to self.");
        }
    }

    // Where different kinds of variables are stored
    public abstract static class VarInfo {
        int offset;

        public int getOffset() {
            return offset;
        }
        abstract public String emitRef(String optionalDest);
        abstract public void emitUpdate(String source);
        public String getRegister() { return null; }
    }
}
