package com.truchsess.util;

/**
 * subinterface of TreeCursor that allows to navigate tree-nodes
 * in a predefined order. The way the order is defined is defined
 * by the type of Tree that returns the instance of SortedTreeCursor.
 * In case of ListTree the order is defined by the index of a child-node.
 * In case of SortedTree the order is defined by an instance of java.util.Comparator
 * being bound to the instance of Tree.
 *
 * @author Norbert von Truchsess
 * @param <E>
 */
public interface SortedTreeCursor<E> extends TreeCursor<E> {

    public void first();

    public void last();

    public boolean hasPrevious();

    public void previous();

    public int level();

}
