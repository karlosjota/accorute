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

public class ExpandableSelectableListTreeModelBase<E> extends MutableListTreeModelBase<E, ExpandableSelectableNode> implements ExpandableTreeModel, SelectableTreeModel {

    public ExpandableSelectableListTreeModelBase(Tree<E> tree) {
        super(tree, ExpandableSelectableNodeImpl.class);
    }

    public void setSelectedNodes(Set<? extends TreePosition> selectedNodesSet) {
        selectAll();
        Iterator<? extends TreePosition> selectedNodes = selectedNodesSet.iterator();
        while (selectedNodes.hasNext()) {
            MutableListTreeModelCursor<E, ExpandableSelectableNode> cursor = getCursor();
            cursor.setPosition(selectedNodes.next());
            cursor.getModel().select();
        }
    }

    @SuppressWarnings("unchecked")
    public Set<? extends TreePosition> getSelectedNodes() {

        final Set<TreePosition> selectedNodes = new HashSet<TreePosition>();
        try {
            walker.doWalk(this, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                    if (((MutableListTreeModelCursor<E, SelectableNode>) cursor).getModel().isSelected())
                        selectedNodes.add(cursor.getPosition());
                }

                public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                    if (((MutableListTreeModelCursor<E, SelectableNode>) cursor).getModel().isSelected())
                        selectedNodes.add(cursor.getPosition());
                }

                public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                }

            });
        } catch (AbortProcessingException ape) {
            throw new RuntimeException(ape);
        }
        return selectedNodes;
    }

    @SuppressWarnings("unchecked")
    public void deselectAll() {
        try {
            walker.doWalk(this, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, SelectableNode>) cursor).getModel().deselect();
                }

                public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, SelectableNode>) cursor).getModel().deselect();
                }

                public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                }

            });
        } catch (AbortProcessingException ape) {
            throw new RuntimeException(ape);
        }
    }

    @SuppressWarnings("unchecked")
    public void selectAll() {
        try {
            walker.doWalk(this, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, SelectableNode>) cursor).getModel().select();
                }

                public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                    ((MutableListTreeModelCursor<E, SelectableNode>) cursor).getModel().select();
                }

                public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                }

            });
        } catch (AbortProcessingException ape) {
            throw new RuntimeException(ape);
        }
    }

    public void setExpandedNodes(Set<? extends TreePosition> expandedNodesSet) {
        collapseAll();
        Iterator<? extends TreePosition> expandedNodes = expandedNodesSet.iterator();
        while (expandedNodes.hasNext()) {
            MutableListTreeModelCursor<E, ExpandableSelectableNode> cursor = getCursor();
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
            throw new RuntimeException(ape);
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
            throw new RuntimeException(ape);
        }
    }

    private class ExpandableSelectableNodeImpl implements ExpandableSelectableNode {

        private boolean selected = true;

        public void deselect() {
            selected = false;
        }

        public void select() {
            selected = true;
        }

        public boolean isSelected() {
            return selected;
        }

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
