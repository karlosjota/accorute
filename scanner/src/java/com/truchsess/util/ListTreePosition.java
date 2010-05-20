package com.truchsess.util;

/**
 * represents the position of a node in a ListTree. (Holds
 * an collection of keys that may be returned in predefined order to navigate
 * to the represented position).
 * The position is only garanteed to be valid as long as the tree is not modified
 * structurally.
 *
 * @author Norbert von Truchsess
 */
public interface ListTreePosition extends TreePosition {

    /**
     * @return an java.util.Iterator-like object that returns the indizes that
     *         are to be navigated to reach the specified position in a ListTree).
     */
    public IntIterator intIterator();


    /**
     * java.util.Iterator-like interface that allows to iterator over instances of
     * the primitive type int.
     */
    public interface IntIterator {

        public boolean hasNext();

        public int next();
    }
}
