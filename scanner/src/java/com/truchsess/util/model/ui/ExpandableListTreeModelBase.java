package com.truchsess.util.model.ui;

import com.truchsess.util.Tree;
import com.truchsess.util.TreeCursor;
import com.truchsess.util.TreePosition;
import com.truchsess.util.TreeWalker;
import com.truchsess.util.TreeWalker.AbortProcessingException;
import com.truchsess.util.model.MutableListTreeModelBase;
import com.truchsess.util.model.MutableListTreeModelCursor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExpandableListTreeModelBase<E> extends MutableListTreeModelBase<E, ExpandableNode> implements ExpandableTreeModel {

    public ExpandableListTreeModelBase(Tree<E> tree) {
        super(tree, ExpandableNodeImpl.class);
    }

    public void setExpandedNodes(Set<? extends TreePosition> expandedNodesSet) {
        collapseAll();
        Iterator<? extends TreePosition> expandedNodes = expandedNodesSet.iterator();
        while (expandedNodes.hasNext()) {
            MutableListTreeModelCursor<E, ExpandableNode> cursor = getCursor();
            cursor.setPosition(expandedNodes.next());
            cursor.getModel().expand();
        }
    }

    @SuppressWarnings("unchecked")
    public Set<? extends TreePosition> getExpandedNodes() {

        final Set<TreePosition> expandedNodes = new HashSet<TreePosition>();
        try {
            walker.doWalk(this, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                    if (((MutableListTreeModelCursor<E, ExpandableNode>) cursor).getModel().isExpanded())
                        expandedNodes.add(cursor.getPosition());
                }

                public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                    if (((MutableListTreeModelCursor<E, ExpandableNode>) cursor).getModel().isExpanded())
                        expandedNodes.add(cursor.getPosition());
                }

                public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                }

            });
        } catch (AbortProcessingException ape) {
            throw new RuntimeException(ape);
        }
        return expandedNodes;
    }

    @SuppressWarnings("unchecked")
    public void collapseAll() {
        try {
            walker.doWalk(this, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, ExpandableNode>) cursor).getModel().collapse();
                }

                public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, ExpandableNode>) cursor).getModel().collapse();
                }

                public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                }

            });
        } catch (AbortProcessingException ape) {
        }
    }

    @SuppressWarnings("unchecked")
    public void expandAll() {
        try {
            walker.doWalk(this, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, ExpandableNode>) cursor).getModel().expand();
                }

                public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, ExpandableNode>) cursor).getModel().expand();
                }

                public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                }

            });
        } catch (AbortProcessingException ape) {
        }
    }

    private class ExpandableNodeImpl implements ExpandableNode {

        private boolean expanded = true;

        public void collapse() {
            expanded = false;
        }

        public void expand() {
            expanded = true;
        }

        public boolean isExpanded() {
            return expanded;
        }
    }
}
