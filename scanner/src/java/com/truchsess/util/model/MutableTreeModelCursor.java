package com.truchsess.util.model;

public interface MutableTreeModelCursor<E, M> extends TreeModelCursor<E, M> {

    public void setModel(M model);

}
