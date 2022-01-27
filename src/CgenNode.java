
import ast.ClassNode;

import java.util.LinkedList;
import java.util.List;

class CgenNode  {
    /**
     * The parent of this node in the inheritance tree
     */
    private CgenNode parent = null;

    /**
     * The children of this node in the inheritance tree
     */
    private List<CgenNode> children = new LinkedList<>();

    private ClassNode c;

    CgenEnv env;

    /**
     * Does this node correspond to a basic class?
     * (i.e., predefined class rather than user defined class)
     */
    private boolean basic_status;

    /**
     * Constructs a new CgenNode to represent class "c".
     *
     * @param c            the class
     * @param basic_status is this class basic or not
     */
    CgenNode(ClassNode c, boolean basic_status) {
        this.c = c;
        this.basic_status = basic_status;
    }

    void addChild(CgenNode child) {
        children.add(child);
    }

    /**
     * Gets the children of this class
     *
     * @return the children
     */
    List<CgenNode> getChildren() {
        return children;
    }

    /**
     * Sets the parent of this class.
     *
     * @param parent the parent
     */
    void setParentNd(CgenNode parent) {
        assert (this.parent == null);
        assert (parent != null);
        this.parent = parent;
    }

    /**
     * Gets the parent of this class
     *
     * @return the parent
     */
    CgenNode getParentNd() {
        return parent;
    }

    /**
     * Returns true is this is a basic class.
     *
     * @return true or false
     */
    boolean basic() {
        return basic_status;
    }

    public ClassNode getClassNode() {
        return c;
    }
}
