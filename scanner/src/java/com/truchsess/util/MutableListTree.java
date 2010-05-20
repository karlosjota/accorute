package com.truchsess.util;

/**
 * represents a mutable (read/write-access) tree-like structure that
 * allows to access child-nodes by an index of primitive type int.
 * (as in java.util.List).
 *
 * @author Norbert von Truchsess
 * @param <E> runtime type of elements to store in tree-nodes
 */
public interface MutableListTree<E> extends MutableTree<E>, ListTree<E> {

}
