import org.antlr.v4.runtime.*;

import java.io.PrintStream;

public class CoolErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {

        PrintStream s = Utilities.parseError(recognizer.getInputStream().getSourceName(), line);
        Token t;
        if (e != null) {
            t = e.getOffendingToken();
        }
        else {
            t = (CommonToken) offendingSymbol;
        }
        s.append(CoolLexer.VOCABULARY.getSymbolicName(t.getType()));
        s.append(" = ");
        s.println(t.getText());
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    }
}
