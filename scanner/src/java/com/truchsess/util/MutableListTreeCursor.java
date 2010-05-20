package com.truchsess.util;

/**
 * represents a cursor to a mutable (read/write-access) ListTree-structure.
 * Allows to insert child-nodes positioned after the last child-node or at a random position.
 * Allows to insert instances of Tree as a subtree (both as last child or at a random position).
 * Insertion of a tree as subtree copies all references of elements held by the tree to be
 * added as subtree while the instance of tree itself remains unaltered and valid. (This is
 * equvalent to the behaviour of java.util.Collection.addAll(Collection c).
 *
 * @author Norbert von Truchsess
 * @param <E>
 */
public interface MutableListTreeCursor<E> extends ListTreeCursor<E>, MutableTreeCursor<E> {

    public void addChild(E element);

    public void addAsChild(Tree<E> tree);

    public void addChild(int index, E element);

    public void addAsChild(int index, Tree<E> tree);
}
