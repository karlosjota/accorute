package com.truchsess.util;

import java.util.Comparator;

/**
 * represents a tree whiches child-nodes may be navigated in a predefined
 * order. The order is defined by the instance of java.util.Comparator
 * assigned to an instance of SortedTree. Depending on the type of tree the
 * Comparator is applied on the elements itself (as in java.util.SortedSet),
 * or the keys being used to access child-nodes (as in java.util.SortedMap)
 *
 * @author Norbert von Truchsess
 * @param <E> runtime type of elements to store in tree-nodes
 */
public interface SortedTree<E> extends Tree<E> {

    public Comparator<E> comparator();
}
