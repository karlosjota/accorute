package com.truchsess.util.model;

import com.truchsess.util.*;
import com.truchsess.util.TreeWalker.AbortProcessingException;

public class MutableListTreeModelBase<E, M> implements MutableListTreeModel<E> {

    @SuppressWarnings("unchecked")
    protected static final TreeWalker walker = new TreeWalkerBase();
    private MutableListTree<ModelNode> tree;
    @SuppressWarnings("unchecked")
    private Class modelclass;

    @SuppressWarnings("unchecked")
    public MutableListTreeModelBase(Tree<E> intree, final Class modelclass) {
        this.tree = new ArrayListTree<ModelNode>();
        this.modelclass = modelclass;
        final MutableListTreeCursor<ModelNode> thiscursor = (MutableListTreeCursor<ModelNode>) tree.getCursor();
        try {
            walker.doWalk(intree, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                    try {
                        thiscursor.addChild(new ModelNode(cursor.getElement(), (M) modelclass.newInstance()));
                    } catch (IllegalAccessException iae) {
                        throw new AbortProcessingException(iae);
                    } catch (InstantiationException ie) {
                        throw new AbortProcessingException(ie);
                    }
                    thiscursor.down();
                }

                public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                    thiscursor.up();
                    try {
                        thiscursor.addChild(new ModelNode(cursor.getElement(), (M) modelclass.newInstance()));
                    } catch (IllegalAccessException iae) {
                        throw new AbortProcessingException(iae);
                    } catch (InstantiationException ie) {
                        throw new AbortProcessingException(ie);
                    }
                    thiscursor.down();
                    thiscursor.last();
                }

                public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                    thiscursor.up();
                }
            });
        } catch (AbortProcessingException ape) {
            throw new RuntimeException(ape);
        }
    }

    @SuppressWarnings("unchecked")
    private MutableListTreeModelBase(MutableListTree<ModelNode> tree, Class modelclass) {
        this.tree = tree;
        this.modelclass = modelclass;
    }

    public MutableListTreeModelCursor<E, M> getCursor() {
        return getCursor((MutableListTreeCursor<ModelNode>) tree.getCursor());
    }

    private MutableListTreeModelCursor<E, M> getCursor(MutableListTreeCursor<ModelNode> inCursor) {

        final MutableListTreeCursor<ModelNode> cursor = inCursor;

        return new MutableListTreeModelCursor<E, M>() {

            public void setModel(M model) {
                cursor.getElement().model = model;
            }

            public M getModel() {
                return cursor.getElement().model;
            }

            public void setElement(E element) {
                cursor.getElement().element = element;
            }

            public E getElement() {
                return cursor.getElement().element;
            }

            public int level() {
                return cursor.level();
            }

            public void first() {
                cursor.first();
            }

            public void last() {
                cursor.last();
            }

            public boolean hasPrevious() {
                return cursor.hasPrevious();
            }

            public void previous() {
                cursor.previous();
            }

            public boolean hasChildren() {
                return cursor.hasChildren();
            }

            public void down() {
                cursor.down();
            }

            public void down(int index) {
                cursor.down(index);
            }

            public boolean hasNext() {
                return cursor.hasNext();
            }

            public void next() {
                cursor.next();
            }

            public boolean hasParent() {
                return cursor.hasParent();
            }

            public void up() {
                cursor.up();
            }

            public void clear() {
                cursor.clear();
            }

            @SuppressWarnings("unchecked")
            public MutableListTreeModelCursor<E, M> clone() {
                return getCursor((MutableListTreeCursor<ModelNode>) cursor.clone());
            }

            public ListTreePosition getPosition() {
                return (ListTreePosition) cursor.getPosition();
            }

            public MutableListTreeModel<E> remove() {
                return new MutableListTreeModelBase<E, M>((MutableListTree<ModelNode>) cursor.remove(), modelclass);
            }

            public void reset() {
                cursor.reset();
            }

            public void setPosition(TreePosition position) {
                cursor.setPosition(position);
            }

            @SuppressWarnings("unchecked")
            public void addAsChild(int index, TreeModel<E, M> tree) {
                final int rootIndex = index;

                try {
                    walker.doWalk(tree, new TreeWalker.Walk<E>() {

                        boolean isRoot = true;

                        public void doDown(TreeCursor<E> cursor) {
                            if (isRoot) {
                                addChild(rootIndex, cursor.getElement());
                                down(rootIndex);
                                doModel(cursor);
                                isRoot = false;
                            } else {
                                addChild(cursor.getElement());
                                down();
                                doModel(cursor);
                            }
                        }

                        public void doNext(TreeCursor<E> cursor) {
                            up();
                            addChild(cursor.getElement());
                            down(0);
                            last();
                            doModel(cursor);
                        }

                        public void doUp(TreeCursor<E> cursor) {
                            up();
                        }

                        private void doModel(TreeCursor<E> cursor) {
                            M model = ((TreeModelCursor<E, M>) cursor).getModel();
                            setModel(model);
                        }
                    });
                } catch (AbortProcessingException ape) {
                    throw new RuntimeException(ape);
                }
                up();
            }

            @SuppressWarnings("unchecked")
            public void addAsChild(TreeModel<E, M> tree) {
                try {
                    walker.doWalk(tree, new TreeWalker.Walk<E>() {

                        public void doDown(TreeCursor<E> cursor) {
                            addChild(cursor.getElement());
                            down();
                            doModel(cursor);
                        }

                        public void doNext(TreeCursor<E> cursor) {
                            up();
                            addChild(cursor.getElement());
                            down(0);
                            last();
                            doModel(cursor);
                        }

                        public void doUp(TreeCursor<E> cursor) {
                            up();
                        }

                        private void doModel(TreeCursor<E> cursor) {
                            setModel(((TreeModelCursor<E, M>) cursor).getModel());
                        }
                    });
                } catch (AbortProcessingException ape) {
                    throw new RuntimeException(ape);
                }
                up();
            }

            @SuppressWarnings("unchecked")
            public void addAsChild(int index, Tree<E> intree) {
                final int rootIndex = index;
                final MutableListTreeCursor<ModelNode> thiscursor = cursor.clone();
                try {
                    walker.doWalk(intree, new TreeWalker.Walk<E>() {

                        boolean isRoot = true;

                        public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                            if (isRoot) {
                                try {
                                    thiscursor.addChild(rootIndex, new ModelNode(cursor.getElement(), (M) modelclass.newInstance()));
                                } catch (IllegalAccessException iae) {
                                    throw new AbortProcessingException(iae);
                                } catch (InstantiationException ie) {
                                    throw new AbortProcessingException(ie);
                                }
                                down(rootIndex);
                                isRoot = false;
                            } else {
                                try {
                                    thiscursor.addChild(new ModelNode(cursor.getElement(), (M) modelclass.newInstance()));
                                } catch (IllegalAccessException iae) {
                                    throw new AbortProcessingException(iae);
                                } catch (InstantiationException ie) {
                                    throw new AbortProcessingException(ie);
                                }
                                thiscursor.down();
                            }
                        }

                        public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                            thiscursor.up();
                            try {
                                thiscursor.addChild(new ModelNode(cursor.getElement(), (M) modelclass.newInstance()));
                            } catch (IllegalAccessException iae) {
                                throw new AbortProcessingException(iae);
                            } catch (InstantiationException ie) {
                                throw new AbortProcessingException(ie);
                            }
                            thiscursor.down();
                            thiscursor.last();
                        }

                        public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                            thiscursor.up();
                        }
                    });
                } catch (AbortProcessingException ape) {
                    throw new RuntimeException(ape);
                }
            }

            @SuppressWarnings("unchecked")
            public void addAsChild(Tree<E> intree) {
                final MutableListTreeCursor<ModelNode> thiscursor = cursor.clone();
                try {
                    walker.doWalk(intree, new TreeWalker.Walk<E>() {

                        public void doDown(TreeCursor<E> cursor) throws AbortProcessingException {
                            try {
                                thiscursor.addChild(new ModelNode(cursor.getElement(), (M) modelclass.newInstance()));
                            } catch (IllegalAccessException iae) {
                                throw new AbortProcessingException(iae);
                            } catch (InstantiationException ie) {
                                throw new AbortProcessingException(ie);
                            }
                            thiscursor.down();
                        }

                        public void doNext(TreeCursor<E> cursor) throws AbortProcessingException {
                            thiscursor.up();
                            try {
                                thiscursor.addChild(new ModelNode(cursor.getElement(), (M) modelclass.newInstance()));
                            } catch (IllegalAccessException iae) {
                                throw new AbortProcessingException(iae);
                            } catch (InstantiationException ie) {
                                throw new AbortProcessingException(ie);
                            }
                            thiscursor.down();
                            thiscursor.last();
                        }

                        public void doUp(TreeCursor<E> cursor) throws AbortProcessingException {
                            thiscursor.up();
                        }
                    });
                } catch (AbortProcessingException ape) {
                    throw new RuntimeException(ape);
                }
            }

            @SuppressWarnings("unchecked")
            public void addChild(E element) {
                try {
                    cursor.addChild(new ModelNode(element, (M) modelclass.newInstance()));
                } catch (IllegalAccessException iae) {
                    throw new RuntimeException(iae);
                } catch (InstantiationException ie) {
                    throw new RuntimeException(ie);
                }
            }

            @SuppressWarnings("unchecked")
            public void addChild(int index, E element) {
                try {
                    cursor.addChild(index, new ModelNode(element, (M) modelclass.newInstance()));
                } catch (IllegalAccessException iae) {
                    throw new RuntimeException(iae);
                } catch (InstantiationException ie) {
                    throw new RuntimeException(ie);
                }
            }
        };
    }

    public void clear() {
        tree.clear();
    }

    private class ModelNode {

        E element;
        M model;

        ModelNode(E element, M model) {
            this.element = element;
            this.model = model;
        }
    }
}
