
/**
 * This class aggregates all kinds of support routines and constants
 * for the code generator; all routines are statics, so no instance of
 * this class is ever created.
 */
final class CgenConstants {
    /**
     * Runtime constants for controlling the garbage collector.
     */
    final static String[] gcInitNames = {
            "_NoGC_Init", "_GenGC_Init", "_ScnGC_Init"
    };

    /**
     * Runtime constants for controlling the garbage collector.
     */
    final static String[] gcCollectNames = {
            "_NoGC_Collect", "_GenGC_Collect", "_ScnGC_Collect"
    };

    final static int MAXINT = 100000000;
    final static int WORD_SIZE = 4;
    final static int LOG_WORD_SIZE = 2;     // for logical shifts

    // Global names
    final static String CLASSNAMETAB = "class_nameTab";
    final static String CLASSOBJTAB = "class_objTab";
    final static String INTTAG = "_int_tag";
    final static String BOOLTAG = "_bool_tag";
    final static String STRINGTAG = "_string_tag";
    final static String HEAP_START = "heap_start";

    // Naming conventions
    final static String DISPTAB_SUFFIX = "_dispTab";
    final static String METHOD_SEP = ".";
    final static String CLASSINIT_SUFFIX = "_init";
    final static String PROTOBJ_SUFFIX = "_protObj";
    final static String OBJECTPROTOBJ = "Object" + PROTOBJ_SUFFIX;
    final static String INTCONST_PREFIX = "int_const";
    final static String STRCONST_PREFIX = "str_const";
    final static String BOOLCONST_PREFIX = "bool_const";


    final static int EMPTYSLOT = 0;
    final static String LABEL = ":\n";

    // information about object headers
    final static int DEFAULT_OBJFIELDS = 3;
    final static int TAG_OFFSET = 0;
    final static int SIZE_OFFSET = 1;
    final static int DISPTABLE_OFFSET = 2;

    final static int STRING_SLOTS = 1;
    final static int INT_SLOTS = 1;
    final static int BOOL_SLOTS = 1;

    final static String GLOBAL = "\t.globl\t";
    final static String ALIGN = "\t.align\t2\n";
    final static String WORD = "\t.word\t";

    // register names,
    final static String ZERO = "$zero";        // Zero register
    final static String ACC = "$a0";        // Accumulator
    final static String A1 = "$a1";        // For arguments to prim funcs
    final static String SELF = "$s0";        // Ptr to self (callee saves)
    final static String T1 = "$t1";        // Temporary 1
    final static String T2 = "$t2";            // Temporary 2
    final static String T3 = "$t3";            // Temporary 3
    final static String SP = "$sp";        // Stack pointer
    final static String FP = "$fp";        // Frame pointer
    final static String RA = "$ra";        // Return address

    //
    // General-purpose registers for use by the register allocator:
    //
    // $t0, $t1, $t2, $t3, $t4, $v0, $v1, $a0, $a1, $a2, $gp, $s7 are used by
    // Object.copy, _GenGC_Collect, etc.  So don't use them here.
    // And $s0 is spoken for.
    //
    // We are also limited by the GC's register mask
    //
    // These registers are all treated as callee-saves.

    final static int NUM_REGS = 6;
    final static String[] regNames = {"$s1", "$s2", "$s3", "$s4", "$s5","$s6"};

    //returns true if name is one of the registers in regNames.
    //If this returns false, the register might be e.g. ACC, which is frequently
    //overwritten.
    private boolean isAllocatedReg(String reg) {
        for (int i = 0; i < CgenConstants.NUM_REGS; i++)
            if (reg == CgenConstants.regNames[i])
                return true;
        return false;
    }

    //Assume that r1 and r2 are two of the constants (in regNames or the
    //individual constants like ACC), so pointer equality is enough.
    public static boolean regEq(String r1, String r2) { return r1 == r2; }

    // Opcodes
    final static String JALR = "\tjalr\t";
    final static String JAL = "\tjal\t";
    final static String RET = "\tjr\t" + RA + "\t";

    final static String SW = "\tsw\t";
    final static String LW = "\tlw\t";
    final static String LI = "\tli\t";
    final static String LA = "\tla\t";

    final static String MOVE = "\tmove\t";
    final static String NEG = "\tneg\t";
    final static String ADD = "\tadd\t";
    final static String ADDI = "\taddi\t";
    final static String ADDU = "\taddu\t";
    final static String ADDIU = "\taddiu\t";
    final static String DIV = "\tdiv\t";
    final static String MUL = "\tmul\t";
    final static String SUB = "\tsub\t";
    final static String SLL = "\tsll\t";
    final static String BEQZ = "\tbeqz\t";
    final static String BRANCH = "\tb\t";
    final static String BEQ = "\tbeq\t";
    final static String BNE = "\tbne\t";
    final static String BLEQ = "\tble\t";
    final static String BLT = "\tblt\t";
    final static String BGT = "\tbgt\t";

    final static String OBJECT_COPY = "Object.copy";
    final static String GENGC_ASSIGN = "_GenGC_Assign";
    final static String EQUALITY_TEST = "equality_test";
    final static String CASE_ABORT = "_case_abort";
    final static String CASE_ABORT2 = "_case_abort2";
    final static String DISPATCH_ABORT = "_dispatch_abort";


    // The following temporary name will not conflict with any
    // user-defined names.
    final static String TEMP1 = "_1";

    // For function prologues and epilogues: how many registers/stack slots
    // do we need?

    // How many of these temps will be put in registers
    static int getRegisterTemps(int temps) {
        if (Flags.disable_reg_alloc)
            return 0;
        if (CgenConstants.NUM_REGS < temps)
            return CgenConstants.NUM_REGS;
        else
            return temps;
    }
    // How many of these temps will be put on the stack
    static int getStackTemps(int temps) {
        int result = temps - getRegisterTemps(temps);
        assert(result >= 0);
        return result;
    }

    // If the local var denoted by the specified offset is
    // stored in a register, returns the name of that register.
    //
    // Returns null if
    //  1) register allocation is disabled, or
    //  2) the offset refers to a formal var, or
    //  3) the offset refers a local for which there were not enough registers.
    // A negative value of "offset" means it's a local variable.

    static String getRegister(int offset) {
        if (!Flags.disable_reg_alloc && offset < 0) { // local variables
            int reg = -1 - offset;  /// === (-offset -1)
            assert (reg < CgenConstants.NUM_REGS);
            return CgenConstants.regNames[reg];
        }
        else
            return null;
    }

}
