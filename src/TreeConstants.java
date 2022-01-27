
import ast.Symbol;

/**
 * For convenience, this class defines a large number of symbols.  These
 * symbols include the primitive type and method names, as well as fixed
 * names used by the runtime system.
 */

class TreeConstants {

    public static final Symbol arg
            = StringTable.idtable.addString("arg");

    public static final Symbol arg2
            = StringTable.idtable.addString("arg2");

    public static final Symbol Bool
            = StringTable.idtable.addString("Bool");

    public static final Symbol concat
            = StringTable.idtable.addString("concat");

    public static final Symbol cool_abort
            = StringTable.idtable.addString("abort");

    public static final Symbol copy
            = StringTable.idtable.addString("copy");

    public static final Symbol Int
            = StringTable.idtable.addString("Int");

    public static final Symbol in_int
            = StringTable.idtable.addString("in_int");

    public static final Symbol in_string
            = StringTable.idtable.addString("in_string");

    public static final Symbol IO
            = StringTable.idtable.addString("IO");

    public static final Symbol length
            = StringTable.idtable.addString("length");

    public static final Symbol Main
            = StringTable.idtable.addString("Main");

    public static final Symbol main_meth
            = StringTable.idtable.addString("main");

    public static final Symbol No_class
            = StringTable.idtable.addString("_no_class");

    public static final Symbol No_type
            = StringTable.idtable.addString("_no_type");

    public static final Symbol Object_ =
            StringTable.idtable.addString("Object");

    public static final Symbol out_int
            = StringTable.idtable.addString("out_int");

    public static final Symbol out_string
            = StringTable.idtable.addString("out_string");

    public static final Symbol prim_slot
            = StringTable.idtable.addString("_prim_slot");

    public static final Symbol self
            = StringTable.idtable.addString("self");

    public static final Symbol SELF_TYPE
            = StringTable.idtable.addString("SELF_TYPE");

    public static final Symbol Str
            = StringTable.idtable.addString("String");

    public static final Symbol str_field
            = StringTable.idtable.addString("_str_field");

    public static final Symbol substr =
            StringTable.idtable.addString("substr");

    public static final Symbol type_name =
            StringTable.idtable.addString("type_name");

    public static final Symbol val =
            StringTable.idtable.addString("_val");
}
