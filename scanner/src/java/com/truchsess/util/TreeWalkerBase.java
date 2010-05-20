package com.truchsess.util;

public class TreeWalkerBase<E> implements TreeWalker<E> {

    public void doWalk(Tree<E> tree, Walk<E> callback) throws AbortProcessingException {
        TreeCursor<E> cursor = tree.getCursor();
        loop:
        do {
            if (cursor.hasChildren()) {
                cursor.down();
                callback.doDown(cursor);
                continue;
            } else if (cursor.hasNext()) {
                cursor.next();
                callback.doNext(cursor);
                continue;
            } else do {
                if (cursor.hasParent()) {
                    cursor.up();
                    callback.doUp(cursor);
                }
                if (!cursor.hasParent()) {
                    break loop;
                }
            } while (!cursor.hasNext());
            cursor.next();
            callback.doNext(cursor);
        } while (true);
    }
}
