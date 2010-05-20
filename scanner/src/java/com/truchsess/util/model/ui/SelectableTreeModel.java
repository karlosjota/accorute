package com.truchsess.util.model.ui;

import com.truchsess.util.TreePosition;

import java.util.Set;

public interface SelectableTreeModel {

    public void setSelectedNodes(Set<? extends TreePosition> selectedNodesSet);

    /**
     * @return Set<? extends TreePosition> a Set that contains the ListTreePositions of all
     *         selected nodes in the ListTree<E>. If there are no selected nodes an empty Set
     *         is returned.
     */
    public Set<? extends TreePosition> getSelectedNodes();

    public void selectAll();

    public void deselectAll();

}
