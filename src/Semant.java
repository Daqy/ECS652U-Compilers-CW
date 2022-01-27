import ast.ProgramNode;

class Semant {

    public static ClassTable classTable;
    public static SymbolTable symtable = new SymbolTable();

    public static void analyze(ProgramNode program) {

        classTable = new ClassTable(program.getClasses());

        ScopeCheckingVisitor scopecheckVisitor = new ScopeCheckingVisitor();
        program.accept(scopecheckVisitor, null);
        TypeCheckingVisitor typecheckVisitor = new TypeCheckingVisitor();
        program.accept(typecheckVisitor, null);

        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
    }

}
