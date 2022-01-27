import ast.ProgramNode;

public class Backend {

    public static void main(String[] args) {

        args = Flags.handleFlags(args);

        if ((args.length > 0) && (Flags.verbose > 0))
            Utilities.warning(Utilities.ErrorCode.IGNORE_INPUT_FILES);

        ProgramNode program = ASTRebuilder.readAST(null);

        if (program != null)
            Cgen.emit(program);
    }
}
