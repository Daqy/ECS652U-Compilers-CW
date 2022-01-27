import ast.*;
import ast.parser.ASTLexer;
import ast.parser.ASTParser;
import ast.parser.ASTParserBaseVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ASTRebuilder extends ASTParserBaseVisitor<Tree> {

    /** Reads AST from from console */
    public static ProgramNode readAST(String inputfile) {
        ProgramNode program = null;

        try {
            ANTLRInputStream input = null;
            if ((Flags.verbose > 0) && (inputfile != null))
                input = new ANTLRFileStream(inputfile);
            else
                input = new ANTLRInputStream(System.in);

            ASTLexer lexer = new ASTLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            if (Flags.verbose > 0) {
                // print all tokens
                tokens.fill();
                List<Token> alltokens = tokens.getTokens();
                for (Token t : alltokens) {
                    System.out.println(t.toString());
                }
            }

            ASTParser parser = new ASTParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new DiagnosticErrorListener());
            parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);

            ParseTree tree = parser.program();

            if (Flags.verbose > 0)
                System.out.println(tree.toStringTree(parser));

            ASTRebuilder b = new ASTRebuilder();
            program = (ProgramNode) b.visit(tree);

        } catch (FileNotFoundException ex) {
            Utilities.fatalError(Utilities.ErrorCode.OPEN_FILE, inputfile);
        } catch (IOException ex) {
            Utilities.fatalError(Utilities.ErrorCode.EXCEPTION_LEXER);
        }

        return program;
    }


    private int getLINENO(String s) {
        return Integer.parseInt(s);
    }


    @Override
    public Tree visitProgram(ASTParser.ProgramContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        ProgramNode p = new ProgramNode(ln);
        for (ASTParser.CoolClassContext c : ctx.coolClass()) {
            p.add((ClassNode) visitCoolClass(c));
        }
        return p;
    }

    @Override
    public Tree visitCoolClass(ASTParser.CoolClassContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());

        Symbol name = (Symbol) visitType(ctx.type(0));
        Symbol parent = (Symbol) visitType(ctx.type(1));
        Symbol filename = StringTable.stringtable.addString(ctx.STR_CONST().getSymbol().getText());

        ClassNode c = new ClassNode(ln, name, parent, filename);
        if (ctx.feature() != null)
            for (ASTParser.FeatureContext f : ctx.feature())
                c.add((FeatureNode) visitFeature(f));

        return c;
    }

    @Override
    public Tree visitMethod(ASTParser.MethodContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        Symbol return_type = (Symbol) visitType(ctx.type());
        ExpressionNode body = (ExpressionNode) visit(ctx.expr());
        List<FormalNode> formals = new LinkedList<FormalNode>();
        for (ASTParser.FormalContext f : ctx.formal())
            formals.add((FormalNode) visit(f));
        return new MethodNode(ln, name, formals, return_type, body);
    }

    @Override
    public Tree visitAttr(ASTParser.AttrContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        Symbol type = (Symbol) visitType(ctx.type());
        ExpressionNode init;
        init = (ExpressionNode) visit(ctx.expr());
        return new AttributeNode(ln, name, type, init);
    }

    @Override
    public Tree visitFormal(ASTParser.FormalContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        Symbol type = (Symbol) visitType(ctx.type());
        return new FormalNode(ln, name, type);
    }

    @Override
    public Tree visitExpr(ASTParser.ExprContext ctx) {
        ExpressionNode expr = (ExpressionNode) visit(ctx.expr_aux());
        if (ctx.type() != null) {
            Symbol type = (Symbol) visit(ctx.type());
            expr.setType(type);
        }
        return expr;
    }

    @Override
    public Tree visitAssign(ASTParser.AssignContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        ExpressionNode e = (ExpressionNode) visit(ctx.expr());
        return new AssignNode(ln, name, e);
    }

    @Override
    public Tree visitStaticDispatch(ASTParser.StaticDispatchContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        ExpressionNode e = (ExpressionNode) visit(ctx.expr(0));
        List<ExpressionNode> args = new LinkedList<ExpressionNode>();
        for (int i = 1; i < ctx.expr().size(); i++) {
            ASTParser.ExprContext a = ctx.expr(i);
            args.add((ExpressionNode) visit(a));
        }
        Symbol type = (Symbol) visitType(ctx.type());
        return new StaticDispatchNode(ln, e, type, name, args);
    }

    @Override
    public Tree visitDynamicDispatch(ASTParser.DynamicDispatchContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        ExpressionNode e = (ExpressionNode) visit(ctx.expr(0));
        List<ExpressionNode> args = new LinkedList<ExpressionNode>();
        for (int i = 1; i < ctx.expr().size(); i++) {
            ASTParser.ExprContext a = ctx.expr(i);
            args.add((ExpressionNode) visit(a));
        }
        return new DispatchNode(ln, e, name, args);
    }

    @Override
    public Tree visitCond(ASTParser.CondContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        ExpressionNode cond = (ExpressionNode) visit(ctx.expr(0));
        ExpressionNode e1 = (ExpressionNode) visit(ctx.expr(1));
        ExpressionNode e2 = (ExpressionNode) visit(ctx.expr(2));
        return new CondNode(ln, cond, e1, e2);
    }

    @Override
    public Tree visitLoop(ASTParser.LoopContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        ExpressionNode cond = (ExpressionNode) visit(ctx.expr(0));
        ExpressionNode body = (ExpressionNode) visit(ctx.expr(1));
        return new LoopNode(ln, cond, body);
    }

    @Override
    public Tree visitBlock(ASTParser.BlockContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        List<ExpressionNode> le = new LinkedList<ExpressionNode>();
        for (ASTParser.ExprContext e : ctx.expr())
            le.add((ExpressionNode)visit(e));
        return new BlockNode(ln, le);
    }

    @Override
    public Tree visitLet(ASTParser.LetContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        ExpressionNode init = (ExpressionNode) visit(ctx.expr(0));
        ExpressionNode body = (ExpressionNode) visit(ctx.expr(1));
        Symbol name = (Symbol) visitId(ctx.id());
        Symbol type = (Symbol) visitType(ctx.type());
        return new LetNode(ln, name, type, init, body);
    }

    @Override
    public Tree visitCase(ASTParser.CaseContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        List<BranchNode> branches = new LinkedList<BranchNode>();
        for (ASTParser.SimplecaseContext b : ctx.simplecase()) {
            branches.add((BranchNode) visit(b));
        }
        ExpressionNode e = (ExpressionNode) visit(ctx.expr());
        return new CaseNode(ln, e, branches);

    }

    @Override
    public Tree visitNew(ASTParser.NewContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol type = (Symbol) visitType(ctx.type());
        return new NewNode(ln, type);
    }

    @Override
    public Tree visitIsvoid(ASTParser.IsvoidContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        ExpressionNode e = (ExpressionNode) visit(ctx.expr());
        return new IsVoidNode(ln, e);
    }

    @Override
    public Tree visitBinop(ASTParser.BinopContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        ExpressionNode e1 = (ExpressionNode) visit(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visit(ctx.expr(1));
        if (ctx.MUL() != null)
            return new MulNode(ln, e1, e2);
        if (ctx.DIVIDE() != null)
            return new DivideNode(ln, e1, e2);
        if (ctx.PLUS() != null)
            return new PlusNode(ln, e1, e2);
        if (ctx.SUB() != null)
            return new SubNode(ln, e1, e2);
        if (ctx.EQ() != null)
            return new EqNode(ln, e1, e2);
        if (ctx.LEQ() != null)
            return new LEqNode(ln, e1, e2);
        if (ctx.LT() != null)
            return new LTNode(ln, e1, e2);
        throw new RuntimeException("Unknown binary operator");
    }

    @Override
    public Tree visitUnop(ASTParser.UnopContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        ExpressionNode e1 = (ExpressionNode) visit(ctx.expr());
        if (ctx.COMP() != null)
            return new CompNode(ln, e1);
        if (ctx.NEG() != null)
            return new NegNode(ln, e1);
        throw new RuntimeException("Unknown unary operator");

    }

    @Override
    public Tree visitConstInt(ASTParser.ConstIntContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol sym = StringTable.inttable.addString(ctx.INT_CONST().getSymbol().getText());
        return new IntConstNode(ln, sym);
    }

    @Override
    public Tree visitConstStr(ASTParser.ConstStrContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol sym = StringTable.stringtable.addString(ctx.STR_CONST().getSymbol().getText());
        return new StringConstNode(ln, sym);
    }

    @Override
    public Tree visitConstBool(ASTParser.ConstBoolContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        int n = Integer.parseInt(ctx.INT_CONST().getText());
        if (n == 1)
            return new BoolConstNode(ln,true);
        if (n == 0)
            return new BoolConstNode(ln,false);
        throw new RuntimeException("Unknown boolean constant");
    }

    @Override
    public Tree visitObj(ASTParser.ObjContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        return new ObjectNode(ln, name);
    }

    @Override
    public Tree visitNoExpr(ASTParser.NoExprContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        return new NoExpressionNode(ln);
    }

    @Override
    public Tree visitSimplecase(ASTParser.SimplecaseContext ctx) {
        int ln = getLINENO(ctx.LINENO().getSymbol().getText());
        Symbol name = (Symbol) visitId(ctx.id());
        Symbol type = (Symbol) visitType(ctx.type());
        ExpressionNode e = (ExpressionNode) visit(ctx.expr());
        return new BranchNode(ln, name, type, e);
    }

    @Override
    public Tree visitType(ASTParser.TypeContext ctx) {
        return StringTable.idtable.addString(ctx.TYPE().getSymbol().getText());
    }

    @Override
    public Tree visitId(ASTParser.IdContext ctx) {
        return StringTable.idtable.addString(ctx.ID().getSymbol().getText());
    }
}
