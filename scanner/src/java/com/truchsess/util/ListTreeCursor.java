package com.truchsess.util;

/**
 * A ListTreeCursor permits ramdom access to child-nodes by an index of primitive type int.
 * As a consequence each position in the tree may be uniquely described by an ordered set
 * of indizes (which is, what an instance of ListTreePosition represents)
 *
 * @author Norbert von Truchsess
 * @param <E>
 */
public interface ListTreeCursor<E> extends SortedTreeCursor<E> {

    /**
     * moves the cursor to the child-node at the slot specified by index.
     *
     * @param index
     * @throws NoSuchElementException    if the current node does not have child-nodes assigned
     * @throws IndexOutOfBoundsException if the current node does have less children then index.
     */
    public void down(int index);

}
