package com.truchsess.util;

/**
 * <p>represents a cursor to a mutable (read/write-access) MapTree-structure.
 * Allows to insert child-nodes positioned at a random position specified by key.
 * Allows to insert instances of MapTree as a subtree at a random position).
 * Insertion of a tree as subtree copies all references of elements held by the tree to be
 * added as subtree while the instance of tree itself remains unaltered and valid. (This is
 * equvalent to the behaviour of java.util.Collection.addAll(Collection c).</p>
 * <p>Note: there are no methods equivalent to MutableListTreeCursor.addChild(E element) and
 * MutableListTreeCursor.addAsChild(Tree tree), as it doesn't seem to make much sense to
 * generate keys by the tree as it happens with indizes in ListTree. (What would be the logical
 * next key after the last Entry in a Map?)</p>
 *
 * @author Norbert von Truchsess
 * @param <E>
 */
public interface MutableMapTreeCursor<K, E> extends MapTreeCursor<K, E>, MutableTreeCursor<E> {

    public void putChild(K key, E element);

    public void putAsChild(K key, MapTree<K, E> tree);
}
