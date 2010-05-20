package com.truchsess.util.model.ui;

import com.truchsess.util.TreePosition;

import java.util.Set;

public interface ExpandableTreeModel {

    public void expandAll();

    public void collapseAll();

    public void setExpandedNodes(Set<? extends TreePosition> expandedNodesSet);

    public Set<? extends TreePosition> getExpandedNodes();

}
