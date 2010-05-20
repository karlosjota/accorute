package com.truchsess.util.model.ui;

import com.truchsess.util.Tree;
import com.truchsess.util.TreeCursor;
import com.truchsess.util.TreeWalker;
import com.truchsess.util.model.TreeModelCursor;

public class ExpandableTreeWalker<E> implements TreeWalker<E> {

    @SuppressWarnings("unchecked")
    public void doWalk(Tree<E> tree, Walk<E> callback) throws AbortProcessingException {
        TreeCursor<E> cursor = tree.getCursor();
        ExpandableNode model;
        if (cursor.hasChildren()) {
            cursor.down();
            callback.doDown(cursor);
            loop:
            do {
                if (cursor.hasChildren() && ((model = ((TreeModelCursor<E, ExpandableNode>) cursor).getModel()) == null || model.isExpanded())) {
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
            cursor.up();
            callback.doUp(cursor);
        }
    }
}
