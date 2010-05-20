package com.truchsess.util;

/**
 * <p>Mother of all Tree interfaces in package com.truchsess.util</p>
 * <p>A Tree represents an immutable (read-only) collection
 * of Objects of runtime type E organized as a tree-like structure.
 * It is inspired by the java.util.Collections API. This means the API exposes the structure
 * of a tree and allows to store elements of runtime type E at each position in the tree,
 * but hides all implementation-details like the underlying data-structure of the tree-nodes itself.</p>
 * <p>The tree-structure is being navigated by a TreeCursor. This cursor is retrieved by
 * calling getCursor() on an instance of Tree. It's initially located at a position that is
 * virtually 'above' the root-node. This design was choosen to permit the retrival of a valid
 * cursor from an empty tree that does not contain a root-node yet. As a consequence one has
 * to call 'down()' on a new instance of TreeCursor once to move the position of the cursor
 * to the the root-node (if there is one).It is valid to call up() on a cursor positioned at the
 * root-node of a tree once to move it back to the initial position</p>
 * <p>More specialized types of tree (e.g. mutable(read/write-access), with child-nodes accessible in
 * predevined order (as in java.util.SortedSet), via index (as in java.util.List) or Objects as key
 * (as in java.util.Map) are represented by instances implementing the subinterfaces of Tree.</p>
 *
 * @author Norbert von Truchsess
 * @param <E> runtime type of elements to store in tree-nodes
 */
public interface Tree<E> {

    /**
     * Factory method that returns new instances of TreeCursor for this instance of Tree
     *
     * @return TreeCursor pointing to a position virtually above the root-node of the tree.
     */
    public TreeCursor<E> getCursor();

}
