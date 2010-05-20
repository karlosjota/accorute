package com.truchsess.util;

import java.util.Iterator;

public class MapTreeWalker {

    /**
     * Algorithm to walk a MapTree:
     * <p/>
     * 1. If there are any children that have not been visited, go down to the next child that has not been visited yet.
     * 2. If there are no children left to visit, go up.
     *
     * @param <K>
     * @param <E>
     * @param tree
     * @param callback
     * @throws AbortProcessingException
     */
    @SuppressWarnings("unchecked")
    public static <K, E> void doWalk(MapTree<K, E> tree, Walk<K, E> callback) throws AbortProcessingException {
        int len = 10;
        Iterator<K>[] keystack = new Iterator[len];
        int level = -1;
        Iterator<K> keys;
        MapTreeCursor<K, E> cursor = (MapTreeCursor<K, E>) tree.getCursor();
        do {
            if (cursor.hasChildren()) {
                if (++level == len) {
                    Iterator<K>[] tmpKeystack = new Iterator[len + 10];
                    System.arraycopy(keystack, 0, tmpKeystack, 0, len);
                    keystack = tmpKeystack;
                    len += 10;
                }
                if ((keys = keystack[++level]) == null) {
                    keystack[level] = (keys = cursor.getChildKeys().iterator());
                }
                if (keys.hasNext()) {
                    cursor.down(keys.next());
                    callback.doDown(cursor);
                    continue;
                }
            }
            if (cursor.hasParent()) {
                --level;
                cursor.up();
                callback.doUp(cursor);
            }
        } while (cursor.hasParent());
    }

    public interface Walk<K, E> {
        public void doDown(MapTreeCursor<K, E> cursor) throws AbortProcessingException;

        public void doUp(MapTreeCursor<K, E> cursor) throws AbortProcessingException;
    }

    public class AbortProcessingException extends Exception {
        private static final long serialVersionUID = 2835928358243526435L;
    }
}

