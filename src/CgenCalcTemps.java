import ast.*;
import ast.visitor.BaseVisitor;

// calculate the number of temporaries needed to evaluate an expression,
// assuming all computation takes place in a distinguished register
// $a0.   The Cool features which require temporaries are:
//
//   1. binary arithmetic & relational operators
//      (+,*,/,-, <, <=, =)
//
//   2. constructs that introduce new bindings
//      (let, case)
//
//   3. new SELF_TYPE
public class CgenCalcTemps extends BaseVisitor<Void, Void> {

    //  keeps track of the # of temporaries currently in use
    private int num = 0;

    // the maximum # of temporaries ever required.
    private int max = 0;

    private void inc() {
        num++;
        if (num > max) max = num;
    }

    private void dec() {
        num--;
        assert(num>=0);
    }

    /* return maximum number of temps */
    int get() {
        return max;
    }

    @Override
    public Void visit(AttributeNode node, Void data) {
        node.getInit().accept(this, data);
        return null;
    }

    @Override
    public Void visit(BinopNode node, Void data) {
        /* TODO */
        return null;
    }

    @Override
    public Void visit(LetNode node, Void data) {
        ExpressionNode init = node.getInit();
        if (!(init instanceof NoExpressionNode))
            init.accept(this, null);
        inc();
        node.getBody().accept(this, null);
        dec();
        return null;
    }

    @Override
    public Void visit(BranchNode node, Void data) {
        /* TODO */
        return null;
    }

    @Override
    public Void visit(NewNode node, Void data) {
        // new uses one temporary in the case of new SELF_TYPE
        if (node.getType_name() == TreeConstants.SELF_TYPE) {
            inc();
            dec();
        }
        return null;
    }
}
