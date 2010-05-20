package com.truchsess.util;

import java.util.Iterator;

/**
 * represents the position of a node in a MapTree. (Holds
 * an collection of keys that may be returned in predefined order to navigate
 * to the represented position).
 * The position is only garanteed to be valid as long as the tree is not modified
 * structurally.
 *
 * @author Norbert von Truchsess
 * @param <K>
 */
public interface MapTreePosition<K> extends TreePosition {

    /**
     * @return an java.util.Iterator over the keys to navigate to this position
     *         (beginning at the root-node).
     */
    public Iterator<K> iterator();
}
