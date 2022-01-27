
import ast.Symbol;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class CgenEmit  {

    private int stringclasstag;
    private int intclasstag;
    private int boolclasstag;

    /**
     * This is the stream to which assembly instructions are output
     */
    private PrintStream s = createPrintStream();

    public PrintStream getStream() {
        return s;
    }

    public CgenEmit(int st, int it, int bt) {
        //this.classTable = classTable;
        this.stringclasstag = st;
        this.intclasstag = it;
        this.boolclasstag = bt;
    }

    private PrintStream createPrintStream() {

        PrintStream output = System.out;
        String filename = null;
        if (Flags.out_filename == null) {
            if (Flags.in_filename != null) {
                filename = Flags.in_filename.substring(0,
                        Flags.in_filename.lastIndexOf('.'))
                        + ".s";
            }
        } else {
            filename = Flags.out_filename;
        }

        if (filename != null) {
            try {
                output = new PrintStream(new FileOutputStream(filename));
            } catch (IOException ex) {
                Utilities.fatalError("Cannot open output file " + filename);
            }
        }
        return output;
    }


    // The following methods emit code for constants and global
    // declarations.

    /**
     * Emits code to start the .data segment and to
     * declare the global names.
     */
    protected void codeGlobalData() {
        // The following global names must be defined first.

        s.print("\t.data\n" + CgenConstants.ALIGN);
        s.println(CgenConstants.GLOBAL + CgenConstants.CLASSNAMETAB);
        s.print(CgenConstants.GLOBAL);
        emitProtObjRef(TreeConstants.Main);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        emitProtObjRef(TreeConstants.Int);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        emitProtObjRef(TreeConstants.Str);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        codeRef(false);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        codeRef(true);
        s.println("");
        s.println(CgenConstants.GLOBAL + CgenConstants.INTTAG);
        s.println(CgenConstants.GLOBAL + CgenConstants.BOOLTAG);
        s.println(CgenConstants.GLOBAL + CgenConstants.STRINGTAG);

        // We also need to know the tag of the Int, String, and Bool classes
        // during code generation.
        s.println(CgenConstants.INTTAG + CgenConstants.LABEL
                + CgenConstants.WORD + intclasstag);
        s.println(CgenConstants.BOOLTAG + CgenConstants.LABEL
                + CgenConstants.WORD + boolclasstag);
        s.println(CgenConstants.STRINGTAG + CgenConstants.LABEL
                + CgenConstants.WORD + stringclasstag);
    }

    /**
     * Generates GC choice constants (pointers to GC functions)
     */
    protected void codeSelectGc() {
        s.println(CgenConstants.GLOBAL + "_MemMgr_INITIALIZER");
        s.println("_MemMgr_INITIALIZER:");
        s.println(CgenConstants.WORD
                + CgenConstants.gcInitNames[Flags.cgen_Memmgr]);

        s.println(CgenConstants.GLOBAL + "_MemMgr_COLLECTOR");
        s.println("_MemMgr_COLLECTOR:");
        s.println(CgenConstants.WORD
                + CgenConstants.gcCollectNames[Flags.cgen_Memmgr]);

        s.println(CgenConstants.GLOBAL + "_MemMgr_TEST");
        s.println("_MemMgr_TEST:");
        s.println(CgenConstants.WORD
                + ((Flags.cgen_Memmgr_Test == Flags.GC_TEST) ? "1" : "0"));
    }

    /**
     * Emits code to reserve space for and initialize all of the
     * constants.  Class names should have been added to the string
     * table (in the supplied code, is is done during the construction
     * of the inheritance graph), and code for emitting string constants
     * as a side effect adds the string's length to the integer table.
     * The constants are emmitted by running through the stringtable and
     * inttable and producing code for each entry.
     */
    protected void codeConstants() {

        // Add constants that are required by the code generator
        StringTable.stringtable.addString("");
        StringTable.inttable.addString("0");

        // Add class names to string table for code generation
        for (Symbol sym : CgenEnv.classTags)
            StringTable.stringtable.addString(sym.getName());

        // Generates code definitions for all string constants in the string table.
        for (Symbol sym : StringTable.stringtable.values())
            codeDefString(sym);

        // Generates code definitions for all int constants in the int table.
        for (Symbol sym : StringTable.inttable.values())
            codeDefInt(sym);

        // Emits code definitions for boolean constants.
        codeDef(false);
        codeDef(true);

    }


    /**
     * Emits code to start the .text segment and to
     * declare the global names.
     */
    protected void codeGlobalText() {
        s.println(CgenConstants.GLOBAL + CgenConstants.HEAP_START);
        s.print(CgenConstants.HEAP_START + CgenConstants.LABEL);
        s.println(CgenConstants.WORD + 0);
        s.println("\t.text");
        s.print(CgenConstants.GLOBAL);
        emitInitRef(TreeConstants.Main);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        emitInitRef(TreeConstants.Int);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        emitInitRef(TreeConstants.Str);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        emitInitRef(TreeConstants.Bool);
        s.println("");
        s.print(CgenConstants.GLOBAL);
        emitMethodRef(TreeConstants.Main, TreeConstants.main_meth);
        s.println("");
    }


    //
    //  The class name table is a mapping from
    //  class tags -> class names.
    //
    protected void codeClassTable()
    {
        s.print(CgenConstants.CLASSNAMETAB);
        s.print(CgenConstants.LABEL);
        for (Symbol c : CgenEnv.classTags) {
            assert (c != null);
            s.print(CgenConstants.WORD);
            codeRefString(c);
            s.println();
        }
    }

    // The class object table is a mapping from
    // class tag -> address of prototype object for the class
    protected void codeProtObjectTable()
    {
        s.print(CgenConstants.CLASSOBJTAB);
        s.print(CgenConstants.LABEL);
        for (Symbol c : CgenEnv.classTags) {
            assert (c != null);
            s.print(CgenConstants.WORD);
            emitProtObjRef(c);
            s.println();
            s.print(CgenConstants.WORD);
            emitInitRef(c);
            s.println();
        }
    }

    protected void codePrototypeObject(CgenEnv env) {

        // Put the -1 header for the garbage collector
        s.print(CgenConstants.WORD);
        s.println("-1");

        emitProtObjRef(env.getClassname());
        s.print(CgenConstants.LABEL);

        // The prototype object must have slots for all attributes of the
        // class, including inherited attributes.  The number of such
        // attributes is the first attribute index of the class plus the
        // number of attributes defined in the class.

        int total_attributes = env.getFirstAttributeOffset() + env.getNumAttributes();
        s.print(CgenConstants.WORD);
        s.println(env.getClassTag());

        s.print(CgenConstants.WORD);
        s.println(total_attributes + CgenConstants.DEFAULT_OBJFIELDS);

        s.print(CgenConstants.WORD);
        emitDispTableRef(env.getClassname());
        s.println();

        for(int i = 0; i < total_attributes; i++)
        {
            s.print(CgenConstants.WORD);

            Symbol type_decl = env.attributeOffsets.lookup(i).getNode().getType_decl();

            if (TreeConstants.Int == type_decl)
                codeRefInt(StringTable.inttable.addString("0"));
            else if (TreeConstants.Str == type_decl)
                codeRefString(StringTable.stringtable.addString(""));
            else if (TreeConstants.Bool == type_decl)
                codeRef(false);
            else
                s.print(CgenConstants.EMPTYSLOT);

            s.println();
        }
    }

    protected void emitNewline() {
        s.println();
    }
    /**
     * Emits an LW instruction.
     *
     * @param dest_reg   the destination register
     * @param offset     the word offset from source register
     * @param source_reg the source register
     */
    protected void emitLoad(String dest_reg, int offset, String source_reg) {
        s.print(CgenConstants.LW);
        s.print(dest_reg);
        s.print(" ");
        s.print(offset * CgenConstants.WORD_SIZE);
        s.print("(");
        s.print(source_reg);
        s.print(")");
        s.println();
    }

    /**
     * Emits an SW instruction.
     *
     * @param dest_reg   the destination register
     * @param offset     the word offset from source register
     * @param source_reg the source register
     */
    protected void emitStore(String source_reg, int offset, String dest_reg
    ) {
        s.println(CgenConstants.SW + source_reg + " "
                + offset * CgenConstants.WORD_SIZE
                + "(" + dest_reg + ")");
    }

    /**
     * Emits the LI instruction.
     *
     * @param dest_reg the destination register
     * @param val      the integer value
     */
    protected void emitLoadImm(String dest_reg, int val) {
        s.print(CgenConstants.LI);
        s.print(dest_reg);
        s.print(" ");
        s.print(val);
        s.println();
    }

    /**
     * Emits an LA instruction.
     *
     * @param dest_reg the destination register
     * @param address  the address from which a word is loaded
     */
    protected void emitLoadAddress(String dest_reg, String address) {
        s.print(CgenConstants.LA);
        s.print(dest_reg);
        s.print(" ");
        s.print(address);
        s.println();
    }

    /**
     * Emits an LA instruction without the address part.
     *
     * @param dest_reg the destination register
     */
    protected void emitPartialLoadAddress(String dest_reg) {
        s.print(CgenConstants.LA);
        s.print(dest_reg);
        s.print(" ");
    }

    /**
     * Emits an instruction to load a boolean constant into a register.
     *
     * @param dest_reg the destination register
     * @param b        the boolean constant
     */
    protected void emitLoadBool(String dest_reg, Boolean b) {
        emitPartialLoadAddress(dest_reg);
        codeRef(b);
        s.println("");
    }

    /**
     * Emits an instruction to load a string constant into a register.
     *
     * @param dest_reg the destination register
     * @param str      the string constant
     */
    protected void emitLoadString(String dest_reg, Symbol str) {
        emitPartialLoadAddress(dest_reg);
        codeRefString(str);
        s.println("");
    }

    /**
     * Emits an instruction to load an integer constant into a register.
     *
     * @param dest_reg the destination register
     * @param i        the integer constant
     */
    protected void emitLoadInt(String dest_reg, Symbol i) {
        emitPartialLoadAddress(dest_reg);
        codeRefInt(i);
        s.println("");
    }

    /**
     * Emits a MOVE instruction.
     *
     * @param dest_reg   the destination register
     * @param source_reg the source register
     */
    protected void emitMove(String dest_reg, String source_reg) {
        if (CgenConstants.regEq(dest_reg, source_reg))
        {
            if (Flags.cgen_debug)
            {
                System.err.println("    Omitting move from "
                        + source_reg + " to " + dest_reg);
                s.print("#");
            }
            else
                return;
        }
        s.println(CgenConstants.MOVE + dest_reg + " " + source_reg);
    }

    /**
     * Emits a NEG instruction.
     *
     * @param dest_reg   the destination register
     * @param source_reg the source register
     */
    protected void emitNeg(String dest_reg, String source_reg) {
        s.println(CgenConstants.NEG + dest_reg + " " + source_reg);
    }

    /**
     * Emits a binary operation instruction.
     *
     * @param opcode the destination register
     * @param dest_reg the destination register
     * @param src1     the source register 1
     * @param src2     the source register 2
     */
    protected void emitBinop(String opcode, String dest_reg, String src1, String src2) {
        s.println(opcode + dest_reg + " " + src1 + " " + src2);
    }

    /**
     * Emits an ADD instruction.
     *
     * @param dest_reg the destination register
     * @param src1     the source register 1
     * @param src2     the source register 2
     */
    protected void emitAdd(String dest_reg, String src1, String src2) {
        s.println(CgenConstants.ADD + dest_reg + " " + src1 + " " + src2);
    }

    /**
     * Emits an ADDU instruction.
     *
     * @param dest_reg the destination register
     * @param src1     the source register 1
     * @param src2     the source register 2
     */
    protected void emitAddu(String dest_reg, String src1, String src2
    ) {
        s.println(CgenConstants.ADDU + dest_reg + " " + src1 + " " + src2);
    }

    /**
     * Emits an ADDIU instruction.
     *
     * @param dest_reg the destination register
     * @param src      the source register
     * @param imm      the immediate
     */
    protected void emitAddiu(String dest_reg, String src, int imm
    ) {
        s.println(CgenConstants.ADDIU + dest_reg + " " + src + " " + imm);
    }

    /**
     * Emits a DIV instruction.
     *
     * @param dest_reg the destination register
     * @param src1     the source register 1
     * @param src2     the source register 2
     */
    protected void emitDiv(String dest_reg, String src1, String src2
    ) {
        s.println(CgenConstants.DIV + dest_reg + " " + src1 + " " + src2);
    }

    /**
     * Emits a MUL instruction.
     *
     * @param dest_reg the destination register
     * @param src1     the source register 1
     * @param src2     the source register 2
     */
    protected void emitMul(String dest_reg, String src1, String src2) {
        s.println(CgenConstants.MUL + dest_reg + " " + src1 + " " + src2);
    }

    /**
     * Emits a SUB instruction.
     *
     * @param dest_reg the destination register
     * @param src1     the source register 1
     * @param src2     the source register 2
     */
    protected void emitSub(String dest_reg, String src1, String src2) {
        s.println(CgenConstants.SUB + dest_reg + " " + src1 + " " + src2);
    }

    /**
     * Emits an SLL instruction.
     *
     * @param dest_reg the destination register
     * @param src1     the source register 1
     * @param num      the number of bits to shift
     */
    protected void emitSll(String dest_reg, String src1, int num) {
        s.println(CgenConstants.SLL + dest_reg + " " + src1 + " " + num);
    }

    /**
     * Emits a JALR instruction.
     *
     * @param dest_reg the register with target address
     */
    protected void emitJalr(String dest_reg) {
        s.println(CgenConstants.JALR + dest_reg);
    }

    /**
     * Emits a JAL instruction.
     *
     * @param dest the target address or label
     */
    protected void emitJal(String dest) {
        s.println(CgenConstants.JAL + dest);
    }

    /**
     * Emits a RET instruction.
     */
    protected void emitReturn() {
        s.println(CgenConstants.RET);
    }

    /**
     * Emits a call to gc_assign.
     */
    protected void emitGenGCAssign() {
        s.println(CgenConstants.JAL + CgenConstants.GENGC_ASSIGN);
    }

    protected void emitCopy() {
        s.println(CgenConstants.JAL + CgenConstants.OBJECT_COPY);
    }

    protected void emitEqualityTest() {
        s.println(CgenConstants.JAL + CgenConstants.EQUALITY_TEST);
    }

    protected void emitCaseAbort() {
        s.println(CgenConstants.JAL + CgenConstants.CASE_ABORT);
    }

    protected void emitCaseAbort2() {
        s.println(CgenConstants.JAL + CgenConstants.CASE_ABORT2);
    }

    protected void emitDispatchAbort() {
        s.println(CgenConstants.JAL + CgenConstants.DISPATCH_ABORT);
    }

    /**
     * Emits a BEQZ instruction.
     *
     * @param src   the source register
     * @param label the label number
     */
    protected void emitBeqz(String src, int label) {
        s.print(CgenConstants.BEQZ + src + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a BEQ instruction.
     *
     * @param src1  the source register 1
     * @param src2  the source register 2
     * @param label the label number
     */
    protected void emitBeq(String src1, String src2, int label) {
        s.print(CgenConstants.BEQ + src1 + " " + src2 + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a BNE instruction.
     *
     * @param src1  the source register 1
     * @param src2  the source register 2
     * @param label the label number
     */
    protected void emitBne(String src1, String src2, int label) {
        s.print(CgenConstants.BNE + src1 + " " + src2 + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a conditional branch instruction.
     *
     * @param opcode the operation
     * @param src1  the source register 1
     * @param src2  the source register 2
     * @param label the label number
     */
    protected void emitBranchCond(String opcode, String src1, String src2, int label) {
        s.print(opcode + src1 + " " + src2 + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a BLEQ instruction.
     *
     * @param src1  the source register 1
     * @param src2  the source register 2
     * @param label the label number
     */
    protected void emitBleq(String src1, String src2, int label) {
        s.print(CgenConstants.BLEQ + src1 + " " + src2 + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a BLT instruction.
     *
     * @param src1  the source register 1
     * @param src2  the source register 2
     * @param label the label number
     */
    protected void emitBlt(String src1, String src2, int label) {
        s.print(CgenConstants.BLT + src1 + " " + src2 + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a BLTI instruction.
     *
     * @param src   the source register
     * @param imm   the immediate
     * @param label the label number
     */
    protected void emitBlti(String src, int imm, int label) {
        s.print(CgenConstants.BLT + src + " " + imm + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a BGTI instruction.
     *
     * @param src   the source register
     * @param imm   the immediate
     * @param label the label number
     */
    protected void emitBgti(String src, int imm, int label) {
        s.print(CgenConstants.BGT + src + " " + imm + " ");
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emits a BRANCH instruction.
     *
     * @param label the label number
     */
    protected void emitBranch(int label) {
        s.print(CgenConstants.BRANCH);
        emitLabelRef(label);
        s.println("");
    }

    /**
     * Emit a sequence of instructions to push a register onto stack.
     * Stack grows toward smaller addresses.
     *
     * @param reg the register
     */
    protected void emitPush(String reg) {
        emitStore(reg, 0, CgenConstants.SP);
        emitAddiu(CgenConstants.SP, CgenConstants.SP, -4);
    }

    /**
     * Emits code to fetch the integer value of the Integer object.
     *
     * @param source a pointer to the Integer object
     * @param dest   the destination register for the value
     */
    protected void emitFetchInt(String dest, String source) {
        emitLoad(dest, CgenConstants.DEFAULT_OBJFIELDS, source);
    }

    /**
     * Emits code to store the integer value of the Integer object.
     *
     * @param source an integer value
     * @param dest   the pointer to an Integer object
     */
    protected void emitStoreInt(String source, String dest) {
        emitStore(source, CgenConstants.DEFAULT_OBJFIELDS, dest);
    }

    /**
     * Emits a reference to dispatch table.
     *
     * @param sym the name of the class
     */
    protected void emitDispTableRef(Symbol sym) {
        s.print(sym + CgenConstants.DISPTAB_SUFFIX);
    }

    /**
     * Emits a reference to class' init() method.
     *
     * @param sym the name of the class
     */
    protected void emitInitRef(Symbol sym) {
        s.print(sym + CgenConstants.CLASSINIT_SUFFIX);
    }

    /**
     * Emits a reference to class' prototype object.
     *
     * @param sym the name of the class
     */
    protected void emitProtObjRef(Symbol sym) {
        s.print(sym + CgenConstants.PROTOBJ_SUFFIX);
    }

    /**
     * Emits a reference to a method in a class
     *
     * @param classname  the name of the class
     * @param methodname the name of the method
     */
    protected void emitMethodRef(Symbol classname,
                       Symbol methodname) {
        s.print(classname + CgenConstants.METHOD_SEP + methodname);
    }

    /**
     * Emits a reference to a label
     *
     * @param label the label number
     */
    protected void emitLabelRef(int label) {
        s.print("label" + label);
    }

    protected void emitInit(Symbol classname) {
        s.print(CgenConstants.JAL);
        emitInitRef(classname);
        s.println();
    }

    /**
     * Emits a definition of a label
     *
     * @param label the label number
     */
    protected void emitLabelDef(int label) {
        emitLabelRef(label);
        s.println(":");
    }

    /**
     * Emits code to manipulate garbage collector
     */
    protected void emitTestCollector() {
        emitPush(CgenConstants.ACC);
        emitMove(CgenConstants.ACC, CgenConstants.SP);
        emitMove(CgenConstants.A1, CgenConstants.ZERO);
        s.println(CgenConstants.JAL + CgenConstants.gcCollectNames[Flags.cgen_Memmgr]);
        emitAddiu(CgenConstants.SP, CgenConstants.SP, 4);
        emitLoad(CgenConstants.ACC, 0, CgenConstants.SP);
    }

    /**
     * Emits code to check the garbage collector
     *
     */
    protected void emitGCCheck(String source) {
        if (source != CgenConstants.A1) emitMove(CgenConstants.A1, source);
        s.println(CgenConstants.JAL + "_gc_check");
    }


    private boolean ascii = false;

    /**
     * Switch output mode to ASCII.
     */
    protected void asciiMode() {
        if (!ascii) {
            s.print("\t.ascii\t\"");
            ascii = true;
        }
    }

    /**
     * Switch output mode to BYTE
     */
    protected void byteMode() {
        if (ascii) {
            s.println("\"");
            ascii = false;
        }
    }

    /**
     * Emits a string constant.
     *
     * @param str the string constant
     */
    protected void emitStringConstant(String str) {
        ascii = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            switch (c) {
                case '\n':
                    asciiMode();
                    s.print("\\n");
                    break;
                case '\t':
                    asciiMode();
                    s.print("\\t");
                    break;
                case '\\':
                    byteMode();
                    s.println("\t.byte\t" + (byte) '\\');
                    break;
                case '"':
                    asciiMode();
                    s.print("\\\"");
                    break;
                default:
                    if (c >= 0x20 && c <= 0x7f) {
                        asciiMode();
                        s.print(c);
                    } else {
                        byteMode();
                        s.println("\t.byte\t" + (byte) c);
                    }
            }
        }
        byteMode();
        s.println("\t.byte\t0\t");
    }


    /**
     * Emits a reference to this boolean constant.
     */
    protected void codeRef(Boolean val) {
        s.print(CgenConstants.BOOLCONST_PREFIX + (val ? "1" : "0"));
    }

    /**
     * Generates code for the boolean constant definition.  This method
     * is incomplete; you get to finish it up in programming assignment
     * 5.
     */
    protected void codeDef(Boolean val) {
        // Add -1 eye catcher
        s.println(CgenConstants.WORD + "-1");
        codeRef(val);
        s.print(CgenConstants.LABEL); // label
        s.println(CgenConstants.WORD + boolclasstag); // tag
        s.println(CgenConstants.WORD + (CgenConstants.DEFAULT_OBJFIELDS +
                CgenConstants.BOOL_SLOTS)); // size
        s.print(CgenConstants.WORD);
        /* Add code to reference the dispatch table for class Bool here */
        emitDispTableRef(TreeConstants.Bool);
        s.println();        // dispatch table
        s.print(CgenConstants.WORD);
        s.println(val ? "1" : "0"); // value (0 or 1)
    }


    /**
     * Emits a reference to this string constant.
     */
    protected void codeRefString(Symbol sym) {
        s.print(CgenConstants.STRCONST_PREFIX);
        s.print(sym.getIndex());
    }


    /**
     * Generates code for the string constant definition.  This method
     * is incomplete; you get to finish it up in programming assignment
     * 5.
     */


    protected void codeDefString(Symbol sym) {
        int l = sym.getName().length();
        Symbol lensym = StringTable.inttable.addString(Integer.toString(l));

        // Add -1 eye catcher
        s.println(CgenConstants.WORD + "-1");
        codeRefString(sym);
        s.print(CgenConstants.LABEL); // label
        s.println(CgenConstants.WORD + stringclasstag); // tag
        s.println(CgenConstants.WORD + (CgenConstants.DEFAULT_OBJFIELDS +
                CgenConstants.STRING_SLOTS +
                (sym.getName().length() + 4) / 4)); // object size
        s.print(CgenConstants.WORD);
    	/* Add code to reference the dispatch table for class String here */
        emitDispTableRef(TreeConstants.Str);
        s.println();  // dispatch table
        s.print(CgenConstants.WORD);
        codeRefInt(lensym);
        s.println(); // length
        emitStringConstant(sym.getName()); // ascii string
        s.print(CgenConstants.ALIGN); // align to word
    }


    /**
     * Generates code for the integer constant definition.  This method
     * is incomplete; you get to finish it up in programming assignment
     * 5.
     */
    protected void codeDefInt(Symbol sym) {
        // Add -1 eye catcher
        s.println(CgenConstants.WORD + "-1");
        codeRefInt(sym);
        s.print(CgenConstants.LABEL); // label
        s.println(CgenConstants.WORD + intclasstag); // tag
        s.println(CgenConstants.WORD + (CgenConstants.DEFAULT_OBJFIELDS +
                CgenConstants.INT_SLOTS)); // size
        s.print(CgenConstants.WORD);
        /* Add code to reference the dispatch table for class Int here */
        emitDispTableRef(TreeConstants.Int);
        s.println();        // dispatch table
        s.println(CgenConstants.WORD + sym.getName()); // integer value
    }

    /**
     * Emits a reference to this integer constant.
     */
    protected void codeRefInt(Symbol sym) {
        s.print(CgenConstants.INTCONST_PREFIX + sym.getIndex());
    }

    protected void emitLabel() {
        s.print(CgenConstants.LABEL);
    }

    protected void emitWord() {
        s.print(CgenConstants.WORD);
    }


    protected void prologue(int temps)
    {
        int reg_temps = CgenConstants.getRegisterTemps(temps);
        int stack_temps = CgenConstants.getStackTemps(temps);
        assert ( (reg_temps + stack_temps) == temps);

        emitAddiu(CgenConstants.SP, CgenConstants.SP,-(3 + temps) * CgenConstants.WORD_SIZE);   // allocate frame
        emitStore(CgenConstants.FP, 3 + temps, CgenConstants.SP);               // save caller's FP
        emitStore(CgenConstants.SELF, 2 + temps, CgenConstants.SP);             //  "    "       SELF
        emitStore(CgenConstants.RA, 1 + temps, CgenConstants.SP);               //  "    "       RA
        emitAddiu(CgenConstants.FP, CgenConstants.SP, 4);                           // set new FP
        emitMove(CgenConstants.SELF, CgenConstants.ACC);                            // set SELF register

        //Save callee-save registers
        for(int i = 0; i < reg_temps; i++) {
            //The first slot for saving registers is at FP + num_temps - 1.
            //So the ith register is at FP + num_temps - i - 1
            emitStore(CgenConstants.regNames[i], temps - i - 1, CgenConstants.FP);// save caller's $si
        }

        // Clear temporaries for GC
        if (Flags.cgen_Memmgr != Flags.GC_NOGC)
            for (int i = 0; i < stack_temps; i++)
                emitStore(CgenConstants.ZERO, i, CgenConstants.FP);

        if (Flags.cgen_Memmgr_Debug == Flags.GC_DEBUG)
            emitGCCheck(CgenConstants.SELF);
    }

    protected void epilogue(int temps, int num_formals)
    {
        if (Flags.cgen_Memmgr_Debug == Flags.GC_DEBUG)
            emitGCCheck(CgenConstants.ACC);

        //Restore callee-save registers
        int reg_temps = CgenConstants.getRegisterTemps(temps);
        for(int i = 0; i < reg_temps; i++) {
            emitLoad(CgenConstants.regNames[i], temps - i - 1, CgenConstants.FP); // restore caller's $si
        }

        emitLoad(CgenConstants.FP, 3  + temps, CgenConstants.SP);               // restore caller's FP
        emitLoad(CgenConstants.SELF, 2 + temps, CgenConstants.SP);              // restore caller's SELF
        emitLoad(CgenConstants.RA, 1 + temps, CgenConstants.SP);                // restore caller's RA
        // deallocate frame
        emitAddiu(CgenConstants.SP, CgenConstants.SP, (3 + temps + num_formals) * CgenConstants.WORD_SIZE);
        emitReturn();
    }
}
