package com.truchsess.util;

/**
 * represents a mutable (read/write-access) tree-like structure.
 *
 * @author Norbert von Truchsess
 * @param <E> runtime type of elements to store in tree-nodes
 */
public interface MutableTree<E> extends Tree<E> {

    /**
     * recursivly clear and remove all references to elements and nodes held by this tree;
     */
    public void clear();

}
