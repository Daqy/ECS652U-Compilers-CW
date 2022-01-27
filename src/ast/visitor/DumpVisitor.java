package ast.visitor;

import ast.*;

import java.io.PrintStream;

public class DumpVisitor extends BaseVisitor<Void,String> {
    PrintStream out;
    int n;

    private void inc(){ n=n+2; }

    private void dec(){ n=n-2; }

    // sm: fixed an off-by-one error here; code assumed there were 80 spaces, but
    // in fact only 79 spaces were there; I've made it 80 now
    //                                         1         2         3         4         5         6         7
    //                               01234567890123456789012345678901234567890123456789012345678901234567890123456789
    private static String padding = "                                                                                "; // 80 spaces for padding


    /** Returns the specified amount of space padding
     *
     * @param n the amount of padding
     * */
    private static String getPadding(int n) {
	    if (n > 80) return padding;
	    if (n < 0) return "";
	    return padding.substring(0, n);
    }

    private void print(String s){
        out.print(s);
    }

    private void pad(String s){
        out.print(getPadding(n));
        out.print(s);
    }

    private void padln(String s){
        pad(s + "\n");
    }

    private void padln(Boolean b){
        padln(b.booleanValue() ? "1" : "0");
    }


    @Override
    public Void visit(Symbol sym, String data) {
        padln(sym.getName());
        return null;
    }


    /** Prints an appropriately escaped string
     *
     * @param s the string to print
     * */
    public static void printEscapedString(String s, PrintStream out) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': out.print("\\\\"); break;
                case '\"': out.print("\\\""); break;
                case '\n': out.print("\\n"); break;
                case '\t': out.print("\\t"); break;
                case '\b': out.print("\\b"); break;
                case '\f': out.print("\\f"); break;
                default:
                    if (c >= 0x20 && c <= 0x7f) {
                        out.print(c);
                    } else {
                        String octal = Integer.toOctalString(c);
                        out.print('\\');
                        switch (octal.length()) {
                            case 1:
                                out.print('0');
                            case 2:
                                out.print('0');
                            default:
                                out.print(octal);
                        }
                    }
            }
        }
    }

    /**
     * Dumps a printable representation of current line number
     */
    private void dump_line(TreeNode node) {
        padln("#" + node.getLineNumber());
    }

    public DumpVisitor(PrintStream out) {
        this.out = out;
        this.n = 0;
    }

    @Override
    public Void visit(ProgramNode node, String data) {
        dump_line(node);
        padln("_program");
        inc();
        visit(node.getClasses(), data);
        dec();
        return null;
     }

    @Override
    public Void visit(ClassNode node, String data) {
        dump_line(node);
        padln("_class");
        inc();
        visit(node.getName(), data);
        visit(node.getParent(), data);
        pad("\"");
        printEscapedString(node.getFilename().getName(), out);
        print("\"\n");
        padln("(");
        visit(node.getFeatures(), data);
        padln(")");
        dec();
        return null;
    }

    @Override
    public Void visit(MethodNode node, String data) {
        dump_line(node);
        padln("_method");
        inc();
        super.visit(node, data);
        dec();
        return null;
    }

    @Override
    public Void visit(AttributeNode node, String data) {
        dump_line(node);
        padln("_attr");
        inc();
        super.visit(node, data);
        dec();
        return null;
    }

    @Override
    public Void visit(FormalNode node, String data) {
        dump_line(node);
        padln("_formal");
        inc();
        super.visit(node, data);
        dec();
        return null;
    }

    @Override
    public Void visit(BranchNode node, String data) {
        dump_line(node);
        padln("_branch");
        inc();
        super.visit(node, data);
        dec();
        return null;
    }

    @Override
    public Void visit(ExpressionNode node, String data) {
        if (node.getType() != null)
            padln(": "+ node.getType().getName());
       else
            padln(": "+ "_no_type");
        return null;
    }

    @Override
    public Void visit(AssignNode node, String data) {
        dump_line(node);
        padln("_assign");
        inc();
        super.visit(node, data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(StaticDispatchNode node, String data) {
        dump_line(node);
        padln("_static_dispatch");
        inc();
        super.visit(node.getExpr(),data);
        visit(node.getType_name(), data);
        visit(node.getName(), data);
        padln("(");
        visit(node.getActuals(), data);
        padln(")");
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(DispatchNode node, String data) {
        dump_line(node);
        padln("_dispatch");
        inc();
        super.visit(node.getExpr(),data);
        visit(node.getName(), data);
        padln("(");
        visit(node.getActuals(), data);
        padln(")");
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(CondNode node, String data) {
        dump_line(node);
        padln("_cond");
        inc();
        super.visit(node, data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(LoopNode node, String data) {
        dump_line(node);
        padln("_loop");
        inc();
        super.visit(node,data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(CaseNode node, String data) {
        dump_line(node);
        padln("_typcase");
        inc();
        super.visit(node.getExpr(), data);
        visit(node.getCases(), data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(BlockNode node, String data) {
        dump_line(node);
        padln("_block");
        inc();
        visit(node.getExprs(), data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(LetNode node, String data) {
        dump_line(node);
        padln("_let");
        inc();
        super.visit(node, data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(BinopNode node, String data) {
        dump_line(node);
        padln(data);
        inc();
        super.visit(node, "");
        dec();
        visit((ExpressionNode) node, "");
        return null;
    }

    @Override
    public Void visit(UnopNode node, String data) {
        dump_line(node);
        padln(data);
        inc();
        super.visit(node, "");
        dec();
        visit((ExpressionNode) node, "");
        return null;
    }

    @Override
    public Void visit(PlusNode node, String data) {
        return visit((BinopNode) node, "_plus");
    }

    @Override
    public Void visit(SubNode node, String data) {
        return visit((BinopNode) node, "_sub");
    }

    @Override
    public Void visit(MulNode node, String data) {
        return visit((BinopNode) node, "_mul");
    }

    @Override
    public Void visit(DivideNode node, String data) {
        return visit((BinopNode) node, "_divide");
    }

    @Override
    public Void visit(NegNode node, String data) {
        return visit((UnopNode) node, "_neg");
    }

    @Override
    public Void visit(LTNode node, String data) {
        return visit((BinopNode) node, "_lt");
    }

    @Override
    public Void visit(EqNode node, String data) {
        return visit((BinopNode) node, "_eq");
    }

    @Override
    public Void visit(LEqNode node, String data) {
        return visit((BinopNode) node, "_leq");
    }

    @Override
    public Void visit(CompNode node, String data) {
        return visit((UnopNode) node, "_comp");
    }

    @Override
    public Void visit(IntConstNode node, String data) {
        dump_line(node);
        padln("_int");
        inc();
        visit(node.getVal(), "");
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(BoolConstNode node, String data) {
        dump_line(node);
        padln("_bool");
        inc();
        padln(node.getVal());
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(StringConstNode node, String data) {
        dump_line(node);
        padln("_string");
        inc();
        pad("\"");
        printEscapedString(node.getVal().getName(), out);
        print("\"\n");
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(NewNode node, String data) {
        dump_line(node);
        padln("_new");
        inc();
        visit(node.getType_name(), data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(IsVoidNode node, String data) {
        dump_line(node);
        padln("_isvoid");
        inc();
        super.visit(node, data);
        dec();
        visit((ExpressionNode) node, data);
        return null;
    }

    @Override
    public Void visit(ObjectNode node, String data) {
        dump_line(node);
        padln("_object");
        inc();
        super.visit(node, data);
        dec();
        visit((ExpressionNode) node, data);
        return null;

    }

    @Override
    public Void visit(NoExpressionNode node, String data) {
        dump_line(node);
        padln("_no_expr");
        visit((ExpressionNode) node, data);
        return null;

    }
}
