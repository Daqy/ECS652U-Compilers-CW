package ast.visitor;

import ast.*;

public class BaseVisitor<R,D> implements TreeVisitor<R,D> {

    protected final R ret;

    protected BaseVisitor() {
        ret = null;
    }

    protected BaseVisitor(R r) {
        ret = r;
    }

    protected R base(Tree node, D data) {
        return ret;
    }

    public R visit(Symbol sym, D data) {
        return ret;
    }

    protected R visit(Iterable<? extends Tree> nodes, D data) {
        R r = null;
        if (nodes != null)
            for (Tree node : nodes)
                r = visit(node, data);
        return r;
    }

    public R visit(Tree node, D data) {
        return (node == null) ? null : node.accept(this, data);
    }

    public R visit(TreeNode node, D data) {
        return base(node, data);
    }

    public R visit(ProgramNode node, D data) {
        return visit(node.getClasses(), data);
    }

    public R visit(ClassNode node, D data) {
        return visit(node.getFeatures(), data);
    }

    public R visit(FeatureNode node, D data) {
        return base(node, data);
    }

    public R visit(MethodNode node, D data) {
        visit(node.getName(),data);
        visit(node.getFormals(), data);
        visit(node.getReturn_type(), data);
        visit((Tree) node.getExpr(), data);
        return base(node, data);
    }

    public R visit(AttributeNode node, D data) {
        visit(node.getName(), data);
        visit(node.getType_decl(), data);
        visit((Tree) node.getInit(), data);
        return  base(node, data);
    }

    public R visit(FormalNode node, D data) {
        visit(node.getName(), data);
        visit(node.getType_decl(), data);
        return base(node, data);
    }

    public R visit(BranchNode node, D data) {
        visit(node.getName(), data);
        visit(node.getType_decl(), data);
        return visit((Tree) node.getExpr(), data);
    }
    public R visit(ExpressionNode node, D data) {
        return  visit((Tree) node, data);
    }

    public R visit(AssignNode node, D data) {
        visit(node.getName(), data);
        return  visit((Tree) node.getExpr(), data);
    }

    public R visit(StaticDispatchNode node, D data) {
        visit((Tree) node.getExpr(), data);
        visit(node.getActuals(), data);
        return base(node, data);
    }

    public R visit(DispatchNode node, D data) {
        visit((Tree) node.getExpr(), data);
        visit(node.getActuals(), data);
        return base(node, data);
    }

    public R visit(CondNode node, D data) {
        visit((Tree) node.getCond(), data);
        visit((Tree) node.getThenExpr(), data);
        visit((Tree) node.getElseExpr(), data);
        return base(node, data);
    }

    public R visit(LoopNode node, D data) {
        visit((Tree) node.getCond(), data);
        visit((Tree) node.getBody(), data);
        return base(node, data);
    }


    public R visit(CaseNode node, D data) {
        visit((Tree)node.getExpr(), data);
        visit(node.getCases(), data);
        return base(node, data);
    }

    public R visit(BlockNode node, D data) {
        return visit(node.getExprs(), data);
    }

    public R visit(LetNode node, D data) {
        visit((Tree) node.getIdentifier(), data);
        visit((Tree) node.getType_decl(), data);
        visit((Tree) node.getInit(), data);
        visit((Tree) node.getBody(), data);
        return base(node, data);
    }

    public R visit(BinopNode node, D data) {
        visit((Tree) node.getE1(), data);
        visit((Tree) node.getE2(), data);
        return base(node, data);
    }

    public R visit(UnopNode node, D data) {
        visit((Tree) node.getE1(), data);
        return base(node, data);
    }

    public R visit(IntBinopNode node, D data) {
        return visit((BinopNode) node, data);
    }

    public R visit(BoolBinopNode node, D data) {
        return visit((BinopNode) node, data);
    }

    public R visit(IntUnopNode node, D data) {
        return visit((UnopNode) node, data);
    }

    public R visit(BoolUnopNode node, D data) {
        return visit((UnopNode) node, data);
    }

    public R visit(PlusNode node, D data) {
        return visit((IntBinopNode) node, data);
    }

    public R visit(SubNode node, D data) {
        return visit((IntBinopNode) node, data);
    }

    public R visit(MulNode node, D data) {
        return visit((IntBinopNode) node, data);
    }

    public R visit(DivideNode node, D data)  {
        return visit((IntBinopNode) node, data);
    }

    public R visit(NegNode node, D data)  {
        return visit((IntUnopNode) node, data);
    }

    public R visit(LTNode node, D data) {
        return visit((BoolBinopNode) node, data);
    }

    public R visit(EqNode node, D data) {
        return visit((BoolBinopNode) node, data);
    }

    public R visit(LEqNode node, D data) {
        return visit((BoolBinopNode) node, data);
    }

    public R visit(CompNode node, D data) {
        return visit((BoolUnopNode) node, data);
    }

    public R visit(ConstNode node, D data) {
        return base(node, data);
    }

    public R visit(IntConstNode node, D data) {
        return visit((ConstNode) node, data);
    }

    public R visit(BoolConstNode node, D data) {
        return visit((ConstNode) node, data);
    }

    public R visit(StringConstNode node, D data) {
        return visit((ConstNode) node, data);
    }

    public R visit(NewNode node, D data) {
        return base(node, data);
    }

    public R visit(IsVoidNode node, D data) {
        return visit((Tree) node.getE1(), data);
    }

    public R visit(ObjectNode node, D data) {
        return visit(node.getName(), data);
    }
    public R visit(NoExpressionNode node, D data) { return base(node, data); }
}
