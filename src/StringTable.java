
import ast.Symbol;

import java.util.HashMap;

public class StringTable extends HashMap<String, Symbol> {

    public static StringTable idtable = new StringTable();
    public static StringTable stringtable = new StringTable();
    public static StringTable inttable = new StringTable();

    public static int MAXSIZE = 1024;

    public Symbol addString(String s) {
        if (s.length() > MAXSIZE)
            s = s.substring(0, MAXSIZE);

        /* the following line requires java 8 */
        return this.computeIfAbsent(s, k -> new Symbol(k,this.size()));

        /* before java 8 use this code instead of computeIfAbsent: */
        /*
        Symbol sym  = this.get(s);
        if (sym == null) {
            sym = new Symbol(s, this.size());
            this.put(s, sym);
        }
        return sym;
        */
    }


}
