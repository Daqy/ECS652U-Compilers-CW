
import ast.ProgramNode;
import ast.visitor.DumpVisitor;

public class Runner {

    public static void main(String[] args) {

        args = Flags.handleFlags(args);
        ProgramNode program = Frontend.lexparse(args);

        if (Flags.verbose > 0) {
            DumpVisitor d = new DumpVisitor(System.out);
            d.visit(program, "");
        }

        Semant.analyze(program);

        if (Flags.verbose > 0) {
            DumpVisitor d = new DumpVisitor(System.out);
            d.visit(program, "");
        }

        Cgen.emit(program);
    }
}
