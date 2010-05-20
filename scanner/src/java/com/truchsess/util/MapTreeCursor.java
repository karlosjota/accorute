package com.truchsess.util;

import java.util.Map.Entry;
import java.util.Set;

/**
 * A MapTreeCursor permits ramdom access to child-nodes by keys of runtime type K.
 * (As in java.util.Map). As a consequence each position in the tree may be uniquely
 * described by an ordered set of keys (which is, what an instance of MapTreePosition represents)
 *
 * @author Norbert von Truchsess
 * @param <E>
 */
public interface MapTreeCursor<K, E> extends TreeCursor<E> {

    /**
     * moves the cursor to the child-node at the slot specified by key.
     *
     * @param key
     * @throws IndexOutOfBoundsException if the current node does have less children then index.
     */
    public void down(K key);

    /**
     * @return a Set containing the keys of all child-nodes
     */
    public Set<K> getChildKeys();

    /**
     * @return the key that allows to access the current node by calling down(key) on its parent.
     */
    public K getKey();

    /**
     * @return an instance of java.util.Map.Entry containing references to the current nodes key and element.
     */
    public Entry<K, E> getEntry();

}
