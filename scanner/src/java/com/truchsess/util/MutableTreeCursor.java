package com.truchsess.util;

/**
 * represents a cursor on a mutable (read/write-access) tree. Allows to write elements
 * to existing nodes, detatch a subtree, or recursively clear all references to child-nodes
 * (and their child-nodes) held by the node at the current position.
 *
 * @author Norbert von Truchsess
 * @param <E>
 */
public interface MutableTreeCursor<E> extends TreeCursor<E> {

    /**
     * set the element of the current node.
     *
     * @param element
     * @throws UnsupportedOperationException if the TreeCursor<E> points to the virtual
     *                                       position just above the root-node (if there is any) which is outside of the tree.
     */
    public void setElement(E element);

    /**
     * detache the current node with all attached children from the tree and return it as
     * an independand instance of Tree.
     *
     * @return a Tree of the same runtime type with the current node attached as root.
     */
    public MutableTree<E> remove();

    /**
     * recursivly clear and remove all references held by the children of the current node;
     * the current nodes position in the Tree<E> and the contained element E remain unaltered.
     */
    public void clear();
	}
