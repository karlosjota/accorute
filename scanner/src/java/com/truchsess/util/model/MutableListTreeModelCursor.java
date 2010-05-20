package com.truchsess.util.model;

import com.truchsess.util.MutableListTreeCursor;

public interface MutableListTreeModelCursor<E, M> extends MutableTreeModelCursor<E, M>,
        MutableListTreeCursor<E> {

    public void addAsChild(TreeModel<E, M> tree);

    public void addAsChild(int index, TreeModel<E, M> tree);

}
