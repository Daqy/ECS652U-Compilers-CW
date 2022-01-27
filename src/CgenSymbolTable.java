
/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

import java.util.HashMap;
import java.util.Map;

 /*
 * Cool symbol tables are implemented using Java hashmaps.  Each
 * hashmap represents a scope and associates a symbol with some
 * data. The ``data'' is whatever data the programmer wishes to
 * associate with each identifier.
 */

class CgenSymbolTable<K, D> {

    /* Roll out our own implementation of a symbol table,
       which is a reverse tree. The scope is a linked list
       of maps.
       Java LinkedList won't work because it doesn't know how to share.
     */
    class Scope {
        Map<K, D> map = new HashMap<>();
        Scope enclosing = null;

        Scope() {
        }

        Scope(Scope s) {
            enclosing = s;
        }

        D lookup(K sym) {
            D val = map.get(sym);
            if (val != null)
                return val;
            if (enclosing != null)
                return enclosing.lookup(sym);
            return null;
        }
    }

    private Scope current;

    public CgenSymbolTable() {
        current = new Scope();
    }

    /* The argument provides the enclosing scope */
    public CgenSymbolTable(CgenSymbolTable<K,D> s) {
        current = new Scope(s.current);
    }

    /**
     * Enters a new scope. A scope must be entered before anything
     * can be added to the table.
     */
    public void enterScope() {
        current = new Scope(current);
    }

    /**
     * Exits the most recently entered scope.
     */
    public void exitScope() {
        if (current == null) {
            Utilities.fatalError("existScope: can't remove scope from an empty symbol table.");
        }
        current = current.enclosing;
    }

    /**
     * Adds a new entry to the symbol table.
     *
     * @param id   the symbol
     * @param info the data associated with id
     */
    public void addId(K id, D info) {
        if (current == null) {
            Utilities.fatalError("addId: can't add a symbol without a scope.");
        }
        current.map.put(id, info);
    }

    /**
     * Looks up an item through all scopes of the symbol table.  If
     * found it returns the associated information field, if not it
     * returns <code>null</code>.
     *
     * @param sym the symbol
     * @return the info associated with sym, or null if not found
     */
    public D lookup(K sym) {
        if (current == null) {
            Utilities.fatalError("lookup: no scope in symbol table.");
        }
        return current.lookup(sym);
    }

    /**
     * Probes the symbol table.  Check the top scope (only) for the
     * symbol <code>sym</code>.  If found, return the information field.
     * If not return <code>null</code>.
     *
     * @param sym the symbol
     * @return the info associated with sym, or null if not found
     */
    public D probe(K sym) {
        if (current == null) {
            Utilities.fatalError("lookup: no scope in symbol table.");
        }
        return current.map.get(sym);
    }

    /**
     * Gets the string representation of the symbol table.
     *
     * @return the string rep
     */
    public String toString() {

        if (current == null)
            return "";

        StringBuilder buf = new StringBuilder();
        Scope tmp = current;
        int i = 0;
        while (tmp != null) {
            buf.append("Scope ").append(i).append(": ").append(tmp.map).append("\n");
            tmp = tmp.enclosing;
        }
        return buf.toString();
    }
}
