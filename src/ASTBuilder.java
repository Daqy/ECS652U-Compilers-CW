import ast.ClassNode;
import ast.ProgramNode;
import ast.*;
import java.util.LinkedList;
import java.util.List;
public class ASTBuilder extends CoolParserBaseVisitor<Tree> {

    @Override
    public Tree visitProgram(CoolParser.ProgramContext ctx) {
        ProgramNode p = new ProgramNode(ctx.getStart().getLine()); 
        for (CoolParser.CoolClassContext c:ctx.coolClass()) {
            p.add((ClassNode)visitCoolClass(c));
        }
        return p;
    }

    @Override
    public Tree visitCoolClass(CoolParser.CoolClassContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol name = StringTable.idtable.addString(ctx.TYPE(0).toString());
        Symbol parent = TreeConstants.Object_;
        if (ctx.INHERITS() != null) {
            parent = StringTable.idtable.addString(ctx.TYPE(1).toString());
        }
        Symbol filename = StringTable.stringtable.addString(ctx.getStart().getTokenSource().getSourceName());
        ClassNode c = new ClassNode(ln, name, parent, filename);
        if (ctx.feature() != null) {
            for (CoolParser.FeatureContext f : ctx.feature())
                c.add((FeatureNode)visitFeature(f));
        }
        return c;
    }

    @Override
    public Tree visitAttribute(CoolParser.AttributeContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        Symbol type = StringTable.idtable.addString(ctx.TYPE().toString());
        ExpressionNode init = new NoExpressionNode(ln);
        if (ctx.expr() != null) {
            init = (ExpressionNode)visitExpr(ctx.expr());
        }
        return new AttributeNode(ln, name, type, init);
    }
    @Override
    public Tree visitMethod(CoolParser.MethodContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        Symbol return_type = StringTable.idtable.addString(ctx.TYPE().toString());
        List<FormalNode> formals = new LinkedList<FormalNode>();

        ExpressionNode body = new NoExpressionNode(ln);
        if (ctx.expr() != null) {
            body = (ExpressionNode)visitExpr(ctx.expr());
        }
        for (CoolParser.FormalContext f : ctx.formal())
            formals.add((FormalNode) visitFormal(f));
        return new MethodNode(ln, name, formals, return_type, body);
    }
    @Override
    public Tree visitFormal(CoolParser.FormalContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        Symbol type = StringTable.idtable.addString(ctx.TYPE().toString());
        return new FormalNode(ln, name, type);
    }
    public Tree visitExpr(CoolParser.ExprContext ctx) {
        if (ctx instanceof CoolParser.AssignmentExpressionContext) {
            return (AssignNode) visitAssignmentExpression((CoolParser.AssignmentExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.IntegerExpressionContext) {
            return (IntConstNode) visitIntegerExpression((CoolParser.IntegerExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.WhileExpressionContext) {
            return (LoopNode) visitWhileExpression((CoolParser.WhileExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.VoidExpressionContext) {
            return (IsVoidNode) visitVoidExpression((CoolParser.VoidExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.NewExpressionContext) {
            return (NewNode) visitNewExpression((CoolParser.NewExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.NotExpressionContext) {
            return (CompNode) visitNotExpression((CoolParser.NotExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.TildeExpressionContext) {
            return (NegNode) visitTildeExpression((CoolParser.TildeExpressionContext) ctx);
        } 
        if (ctx instanceof CoolParser.LeftArrowExpressionContext) {
            return (LEqNode) visitLeftArrowExpression((CoolParser.LeftArrowExpressionContext) ctx);
        } 
        if (ctx instanceof CoolParser.LetExpressionContext) {
            return (LetNode) visitLetExpression((CoolParser.LetExpressionContext) ctx);
        } 
        if (ctx instanceof CoolParser.EqualExpressionContext) {
            return (EqNode) visitEqualExpression((CoolParser.EqualExpressionContext) ctx);
        } 
        if (ctx instanceof CoolParser.MinusExpressionContext) {
            return (SubNode) visitMinusExpression((CoolParser.MinusExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.PlusExpressionContext) {
            return (PlusNode) visitPlusExpression((CoolParser.PlusExpressionContext) ctx);
        } 
        if (ctx instanceof CoolParser.MultiplyExpressionContext) {
            return (MulNode) visitMultiplyExpression((CoolParser.MultiplyExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.DivideExpressionContext) {
            return (DivideNode) visitDivideExpression((CoolParser.DivideExpressionContext) ctx);
        }      
        if (ctx instanceof CoolParser.BlockExpressionContext) {
            return (BlockNode) visitBlockExpression((CoolParser.BlockExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.IdExpressionContext) {
            return (ObjectNode) visitIdExpression((CoolParser.IdExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.StringExpressionContext) {
            return (StringConstNode) visitStringExpression((CoolParser.StringExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.AtDynamicExpressionContext) {
            return (DispatchNode) visitAtDynamicExpression((CoolParser.AtDynamicExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.AtStaticExpressionContext) {
            return (StaticDispatchNode) visitAtStaticExpression((CoolParser.AtStaticExpressionContext) ctx);
        }      
        if (ctx instanceof CoolParser.CondExpressionContext) {
            return (CondNode) visitCondExpression((CoolParser.CondExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.CaseExpressionContext) {
            return (CaseNode) visitCaseExpression((CoolParser.CaseExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.LessThanExpressionContext) {
            return (LTNode) visitLessThanExpression((CoolParser.LessThanExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.BooleanExpressionContext) {
            return (BoolConstNode) visitBooleanExpression((CoolParser.BooleanExpressionContext) ctx);
        }
        if (ctx instanceof CoolParser.BracketExpressionContext) {
            return (ExpressionNode) visitExpr((CoolParser.ExprContext) ctx.getChild(1));
        }
        if (ctx instanceof CoolParser.IdBraceExpressionContext) {
            return (DispatchNode) visitIdBraceExpression((CoolParser.IdBraceExpressionContext) ctx);
        }
        // if (ctx instanceof CoolParser.SpecialCaseContext) {
        //     return (BlockNode) visitSpecialCaseContext((CoolParser.SpecialCaseContext) ctx);
        // }

         // is 5, other than that prints an [] of different numbers
        // System.out.println(ctx.getPayload() instanceof CoolParser.IntegerExpressionContext);
        return new NoExpressionNode(0);
    }

    @Override
    public Tree visitAssignmentExpression(CoolParser.AssignmentExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        return new AssignNode(ln, name, expr);
    }

    @Override
    public Tree visitIntegerExpression(CoolParser.IntegerExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol val = StringTable.inttable.addString(ctx.INT().getSymbol().getText());
        return new IntConstNode(ln, val);
    };

    @Override
    public Tree visitWhileExpression(CoolParser.WhileExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode cond = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode body = (ExpressionNode) visitExpr(ctx.expr(1));
        return new LoopNode(ln, cond, body);
    }

    @Override
    public Tree visitVoidExpression(CoolParser.VoidExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        return new IsVoidNode(ln, expr);
    }

    @Override
    public Tree visitNewExpression(CoolParser.NewExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol name = StringTable.idtable.addString(ctx.TYPE().toString());
        return new NewNode(ln, name);
    }
    
    @Override
    public Tree visitNotExpression(CoolParser.NotExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr());
        return new CompNode(ln, e1);
    }

    @Override
    public Tree visitTildeExpression(CoolParser.TildeExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr());
        return new NegNode(ln, e1);
    }
    
    @Override
    public Tree visitLeftArrowExpression(CoolParser.LeftArrowExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visitExpr(ctx.expr(1));
        if (!(e1 instanceof LEqNode) && !(e2 instanceof LEqNode)) {
            return new LEqNode(ln, e1, e2);
        }
        return null;
    }

    @Override
    public Tree visitLetExpression(CoolParser.LetExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol id = StringTable.idtable.addString(ctx.ID(0).toString());
        Symbol type_dec = StringTable.idtable.addString(ctx.TYPE(0).toString());
        ExpressionNode init = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode body = (ExpressionNode) visitExpr(ctx.expr(1));
        return new LetNode(ln, id, type_dec, init, body);
    }

    @Override
    public Tree visitEqualExpression(CoolParser.EqualExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visitExpr(ctx.expr(1));
        return new EqNode(ln, e1, e2);
    }

    @Override
    public Tree visitPlusExpression(CoolParser.PlusExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visitExpr(ctx.expr(1));
        return new PlusNode(ln, e1, e2);
    }

    @Override
    public Tree visitMultiplyExpression(CoolParser.MultiplyExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visitExpr(ctx.expr(1));
        return new MulNode(ln, e1, e2);
    }

    @Override
    public Tree visitDivideExpression(CoolParser.DivideExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visitExpr(ctx.expr(1));
        return new DivideNode(ln, e1, e2);
    }

    @Override
    public Tree visitMinusExpression(CoolParser.MinusExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visitExpr(ctx.expr(1));
        return new SubNode(ln, e1, e2);
    }

    @Override
    public Tree visitBlockExpression(CoolParser.BlockExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        List<ExpressionNode> exprs = new LinkedList<ExpressionNode>();
        for (int i = 0; i < ctx.expr().size(); i++) {
            exprs.add((ExpressionNode) visitExpr(ctx.expr(i)));
        }
        return new BlockNode(ln, exprs);
    }

    @Override
    public Tree visitIdExpression(CoolParser.IdExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol val = StringTable.idtable.addString(ctx.ID().getText());
        return new ObjectNode(ln, val);
    }

    @Override
    public Tree visitStringExpression(CoolParser.StringExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol val = StringTable.idtable.addString(ctx.STR_CONST().toString().replace("\"", ""));
        return new StringConstNode(ln, val);
    }

    @Override
    public Tree visitAtDynamicExpression(CoolParser.AtDynamicExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr(0));
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        List<ExpressionNode> actuals = new LinkedList<ExpressionNode>();
        for (int i = 1; i < ctx.expr().size(); i++) {
            actuals.add((ExpressionNode) visitExpr(ctx.expr(i)));
        }
        return new DispatchNode(ln, expr, name, actuals);
    }

    @Override
    public Tree visitAtStaticExpression(CoolParser.AtStaticExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr(0));
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        List<ExpressionNode> actuals = new LinkedList<ExpressionNode>();
        Symbol type_name = StringTable.idtable.addString(ctx.TYPE().toString());
        for (int i = 1; i < ctx.expr().size(); i++) {
            actuals.add((ExpressionNode) visitExpr(ctx.expr(i)));
        }
        return new StaticDispatchNode(ln, expr, type_name, name, actuals);
    }

    @Override
    public Tree visitCondExpression(CoolParser.CondExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode cond = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode thenE = (ExpressionNode) visitExpr(ctx.expr(1));
        ExpressionNode elseE = (ExpressionNode) visitExpr(ctx.expr(2));
        return new CondNode(ln, cond, thenE, elseE);
    }

    @Override
    public Tree visitCaseExpression(CoolParser.CaseExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        List<BranchNode> cases = new LinkedList<BranchNode>();
        for (CoolParser.SpecialCaseContext sc : ctx.specialCase()) {
            cases.add((BranchNode) visitSpecialCase(sc));
        }
        return new CaseNode(ln, expr, cases);
    }

    @Override
    public Tree visitLessThanExpression(CoolParser.LessThanExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        ExpressionNode e1 = (ExpressionNode) visitExpr(ctx.expr(0));
        ExpressionNode e2 = (ExpressionNode) visitExpr(ctx.expr(1));
        return new LTNode(ln, e1, e2);
    }

    @Override
    public Tree visitBooleanExpression(CoolParser.BooleanExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Boolean bool = ctx.BOOL().toString().equals("true");
        return new BoolConstNode(ln, bool);
    }

    @Override
    public Tree visitIdBraceExpression(CoolParser.IdBraceExpressionContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol val = TreeConstants.self;
        ExpressionNode expr = (ExpressionNode) new ObjectNode(ln, val);
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        List<ExpressionNode> actuals = new LinkedList<ExpressionNode>();
        for (int i = 0; i < ctx.expr().size(); i++) {
            actuals.add((ExpressionNode) visitExpr(ctx.expr(i)));
        }
        return new DispatchNode(ln, expr, name, actuals);
    }

    @Override
    public Tree visitSpecialCase(CoolParser.SpecialCaseContext ctx) {
        int ln = ctx.getStart().getLine();
        Symbol name = StringTable.idtable.addString(ctx.ID().toString());
        Symbol type_decl = StringTable.idtable.addString(ctx.TYPE().toString());
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        return new BranchNode(ln, name, type_decl, expr);
    }
}
