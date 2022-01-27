import ast.ClassNode;
import ast.Symbol;
import ast.TreeNode;

import java.io.PrintStream;
import java.util.EnumMap;

public class Utilities {

    private static int lexErrors = 0;
    private static int parseErrors = 0;
    private static int semantErrors = 0;
    private static PrintStream errorStream = System.err;;

    public static enum ErrorCode {
		OPEN_FILE, EXCEPTION_LEXER, EXCEPTION_PARSER, LEXER_PARSER,

		UNTERMINATED_STRING_CONSTANT,
		TOOLONG_STRING_CONSTANT,
		NULL_IN_STRING,
		EOF_IN_COMMENT,
		EOF_IN_STRING_CONSTANT,
		UNMATCHED_BLOCK_COMMENT_END,
        ERROR_AST_PARSING, IGNORE_INPUT_FILES, ERROR_SEMANT
	}

	private static EnumMap<ErrorCode, String> errorMessages = new EnumMap<ErrorCode, String>(ErrorCode.class);
	static {
			errorMessages.put(ErrorCode.OPEN_FILE, "Could not open input file %s.");
			errorMessages.put(ErrorCode.EXCEPTION_LEXER, "Unexpected exception in lexer");
			errorMessages.put(ErrorCode.EXCEPTION_PARSER, "Unexpected exception in parser");
			errorMessages.put(ErrorCode.LEXER_PARSER, "Compilation halted due to lex and parse errors");

			errorMessages.put(ErrorCode.UNTERMINATED_STRING_CONSTANT, "Unterminated string constant");
			errorMessages.put(ErrorCode.TOOLONG_STRING_CONSTANT, "String constant too long");
			errorMessages.put(ErrorCode.NULL_IN_STRING , "String contains null character");

			errorMessages.put(ErrorCode.EOF_IN_COMMENT, "EOF in comment");
			errorMessages.put(ErrorCode.EOF_IN_STRING_CONSTANT, "EOF in string constant");
			errorMessages.put(ErrorCode.UNMATCHED_BLOCK_COMMENT_END, "Unmatched *)");

			errorMessages.put(ErrorCode.IGNORE_INPUT_FILES, "Input files ignored, reading from stdin*)");

			errorMessages.put(ErrorCode.ERROR_AST_PARSING, "??? unexpected error in coolc.ast parsing ");

			errorMessages.put(ErrorCode.ERROR_SEMANT, "Compilation halted due to static semantic errors.");
	};

    // change this to true to enable table checking
    private static final boolean checkTables = false;

    /** Prints error message and exits
    *
    * @param msg the error message
    * */
	public static void fatalError(String msg) {
        errorStream.println(msg);
		System.exit(1);
	}

    /** Prints error message and exits
     *
     * @param code the error message
     * */
	public static void fatalError(ErrorCode code, Object... args) {
		fatalError(String.format(errorMessages.get(code), args));
	}

	/** Prints error message
    *
    * @param code the error message
    * */
	public static void error(ErrorCode code, Object... args) {
        errorStream.println(String.format(errorMessages.get(code), args));
	}

	/** Prints warning message
    *
    * @param code the error message
    * */
	public static void warning(ErrorCode code, Object... args) {
		System.out.println(String.format(errorMessages.get(code), args));
	}

    /**
     * Prints line number and file name of the given class.
     * <p>
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     */
    public static PrintStream semantError(ClassNode c) {
        return semantError(c.getFilename(), c);
    }

    /**
     * Prints the file name and the line number of the given tree node.
     * <p>
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t        the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     */
    public static PrintStream semantError(Symbol filename, TreeNode t) {
        errorStream.print(filename.getName() + ":" + t.getLineNumber() + ": ");
        return semantError();
    }

    /**
     * Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     */
    public static PrintStream semantError() {
        semantErrors++;
        return errorStream;
    }

    /**
     * Returns true if there are any static semantic errors.
     */
    public static boolean errors() {
        return (semantErrors != 0 || parseErrors != 0 || lexErrors != 0);
    }


    public static PrintStream parseError(String filename, int line) {
        errorStream.print("\"" +  filename + "\", line " + line + ": syntax error at or near ");
        return parseError();
    }

    public static PrintStream parseError() {
        parseErrors++;
        return errorStream;
    }

    public static PrintStream lexError() {
        lexErrors++;
        return errorStream;
    }
}


	    
	

