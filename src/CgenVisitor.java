
import ast.visitor.BaseVisitor;

public class CgenVisitor<R,D> extends BaseVisitor<R,D> {
    Void visit(CgenNode v) {
        for (CgenNode n : v.getChildren())
            visit(n);
        return null;
    }
}
