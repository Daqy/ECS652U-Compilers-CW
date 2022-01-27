import ast.ClassNode;
import ast.ProgramNode;
import ast.visitor.DumpVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Frontend {

    public static void main(String[] args) throws Exception {

        args = Flags.handleFlags(args);

        ProgramNode program = lexparse(args);

        if (program != null) {
            DumpVisitor d = new DumpVisitor(System.out);
            d.visit(program, "");
        }
    }

    public static ProgramNode lexparse(String[] args) {

        ProgramNode program = null;
        ANTLRInputStream input = null;
        for (int i = 0; i < args.length; i++) {
            try {
                String filename = args[i];
                if (Flags.verbose > 0) {
                    System.out.println("#name \"" + filename + "\"");
                }

                input = new ANTLRFileStream(filename);

                CoolLexer lexer = new CoolLexer(input);
                if (Flags.lexer_debug) {
                    lexer.removeErrorListeners();
                    lexer.addErrorListener(new DiagnosticErrorListener());
                }

                CommonTokenStream tokens = new CommonTokenStream(lexer);

                if (Flags.verbose > 0) {
                    // print all tokens
                    tokens.fill();
                    List<Token> alltokens = tokens.getTokens();
                    for (Token t : alltokens) {
                        System.out.println(t.toString());
                    }
                }

                if (Flags.lexer_only) {
                    tokens.fill();
                    List<Token> alltokens = tokens.getTokens();
                    for (Token t : alltokens) {
                        String tokenName = CoolLexer.VOCABULARY.getSymbolicName(t.getType());
                        String tokenText = t.getText();
                        System.out.print("#" + t.getLine() + " "
                                + tokenName + " "
                        );

                        if (tokenName.equals("STRING") || tokenName.equals("ERROR")) {
                            System.out.print("\"");
                            DumpVisitor.printEscapedString(tokenText, System.out);
                            System.out.println("\"");
                        } else
                            System.out.println(tokenText);

                    }
                    continue;
                }

                CoolParser parser = new CoolParser(tokens);

                if (Flags.parser_debug) {
                    parser.removeErrorListeners();
                    parser.addErrorListener(new DiagnosticErrorListener());
                    parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
                } else {
                    parser.removeErrorListeners();
                    parser.addErrorListener(new CoolErrorListener());
                }


                parser.setErrorHandler(new CoolErrorStrategy());

                ParseTree tree = parser.program();

                if (Flags.verbose > 0)
                    System.out.println(tree.toStringTree(parser));

                if (Utilities.errors()) {
                    if (Flags.recover) {
                        Utilities.error(Utilities.ErrorCode.LEXER_PARSER);
                    } else {
                        Utilities.fatalError(Utilities.ErrorCode.LEXER_PARSER);
                    }   
                }


                ASTBuilder builder = new ASTBuilder();
                ProgramNode ast = (ProgramNode) builder.visit(tree);
                if (program == null) {
                    program = ast;
                } else {
                    if (ast != null)
                        for (ClassNode c : ast.getClasses()) {
                            program.add(c);
                        }
                }

            } catch (FileNotFoundException ex) {
                Utilities.fatalError(Utilities.ErrorCode.OPEN_FILE, args[i]);
            } catch (IOException ex) {
                Utilities.fatalError(Utilities.ErrorCode.EXCEPTION_LEXER, ex);
            } catch (Exception ex) {
		ex.printStackTrace();
                Utilities.fatalError(Utilities.ErrorCode.EXCEPTION_PARSER, ex);
            }

        }

        if (Flags.lexer_only)
            System.exit(0);

        return program;
    }
}
