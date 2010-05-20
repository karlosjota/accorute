package com.truchsess.util;

/**
 * A TreeCursor<E> represents the position of a node in a Tree<E>
 * holding elements of parameter-type E. A new instance of TreeCursor<E>
 * is obtained by calling getCursor() on an instance of Tree<E>.
 * A new instance of TreeCursor<E> points to a virtual position just
 * above the root-node of the tree (if there is any).
 * As a consequence the root-node is added to the tree by calling
 * addChild() on the TreeCursor<E> and you have to call down() to let the
 * TreeCursor point to the real root-node of the Tree<E>(if there is any).
 *
 * @author Norbert von Truchsess
 * @param <E>
 */
public interface TreeCursor<E> extends Cloneable {

    /**
     * @return boolean. returns 'true' if the current node has a parent-node.
     *         returns 'true' if the current node is an decestor the root-node. A
     *         subsequent call to up() is expected to succeed.
     */
    public boolean hasParent();

    /**
     * @return boolean. returns 'true' if the current node has at least one child-node.
     *         in this case a subsequent call to down() is expected to succeed;
     */
    public boolean hasChildren();

    /**
     * move the cursor up to the parent of the current node. If the current node
     * is the root-node, the cursor points to the virtual position just above the root-node.
     */
    public void up();

    /**
     * move the cursor down to the first child. The first child is determined by the 'natural'
     * order of the Iterable that holds the children.
     */
    public void down();

    /**
     * @return boolean. returns 'true' if a subsequent call to next() is expected to succeed.
     */
    public boolean hasNext();

    /**
     * move the cursor to the next sibling of the current node. The next sibling is determined by the 'natural'
     * order of the Iterable that holds the siblings.
     */
    public void next();

    /**
     * reset the cursor to the (virtual) position just above the root-node.
     */
    public void reset();

    /**
     * @return an instance of TreePosition that allows to place the cursor on the same
     *         position later by passing it to setPosition
     */
    public TreePosition getPosition();

    /**
     * place the cursor at the position specified by the given instance of TreePosition.
     *
     * @param position
     * @throws ClassCastException in case the runtime type of the TreePosition does not match the runtime type of the TreeCursor.
     */
    public void setPosition(TreePosition position);

    /**
     * @return E the element that is stored in the current node.
     * @throws UnsupportedOperationException if the cursor points to the virtual position just
     *                                       above the root node (which is outside of the tree).
     */
    public E getElement();

    /**
     * override Object.clone()
     *
     * @return an shallow copy of the current TreeCursor that may be used independently.
     * @see java.lang.CloneNotSupportedException
     * @see java.lang.Object#clone()
     */
    public <T extends Object> T clone();
	
}
