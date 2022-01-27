import ast.ProgramNode;
import ast.visitor.DumpVisitor;

public class Analysis {

    public static void main(String[] args) {

        args = Flags.handleFlags(args);

        if ((args.length > 0) && (Flags.verbose > 0))
            Utilities.warning(Utilities.ErrorCode.IGNORE_INPUT_FILES);

        String filename = null;
        // IntelliJ debug code:
//        if ((Flags.semant_debug) && (args.length == 1))
//           filename = args[0];

        ProgramNode program = ASTRebuilder.readAST(filename);

        if (program != null) {
            Semant.analyze(program);

            DumpVisitor d = new DumpVisitor(System.out);
            d.visit(program, "");
        }
    }

}
