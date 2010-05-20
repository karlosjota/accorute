package com.truchsess.util.model;

import com.truchsess.util.TreeCursor;


public interface TreeModelCursor<E, M> extends TreeCursor<E> {

    public M getModel();

}
