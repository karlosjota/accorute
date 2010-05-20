package com.truchsess.util;

/**
 * represents a mutable (read/write-access) tree-like structure that allows to access child-nodes by a key of runtime type K. (as in java.util.Map).
 *
 * @author Norbert von Truchsess
 * @param <K> runtime type of keys
 * @param <E> runtime type of elements to store in tree-nodes
 */
public interface MutableMapTree<K, E> extends MapTree<K, E>, MutableTree<E> {

}
