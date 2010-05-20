package com.truchsess.util;

import java.io.Serializable;
import java.util.*;

/**
 * an implementation of ListTree<E> and MutableTree<E> that
 * stores references to child-nodes in ArrayLists
 *
 * @author Norbert von Truchsess
 * @param <E> runtime type of elements to store in tree-nodes
 * @see java.util.ArrayList
 */
public class ArrayListTree<E> implements ListTree<E>, MutableListTree<E>, Serializable {

    static final long serialVersionUID = 2983649823242434980L;

    private ListTreeNode root;

    @SuppressWarnings("unchecked")
    private static final TreeWalker walker = new TreeWalkerBase();

    public ArrayListTree() {
        this.root = new ListTreeNode();
    }

    private ArrayListTree(ListTreeNode node) {
        this();
        root.children = new SingleListTreeNodeList();
        root.children.add(node);
        node.parent = root;
    }

    public MutableListTreeCursor<E> getCursor() {
        return new ArrayListTreeCursor(this);
    }

    public void clear() {
        root.clear();
    }

    protected class ListTreeNode {

        ListTreeNode parent;

        List<ListTreeNode> children;

        E element;

        ListTreeNode() {
        }

        ListTreeNode(ListTreeNode parent, E element) {
            this.parent = parent;
            this.element = element;
        }

        void clear() {
            if (children != null) {
                if (!children.isEmpty()) {
                    for (ListTreeNode node : children) {
                        node.clear();
                        node.parent = null;
                        node.element = null;
                    }
                    children.clear();
                }
                children = null;
            }
        }
    }

    protected class SingleListTreeNodeList implements List<ListTreeNode> {

        private boolean hasElement = false;

        private ListTreeNode element;

        public boolean add(ListTreeNode element) {
            if (hasElement)
                throw new IllegalArgumentException();
            this.element = element;
            hasElement = true;
            return true;
        }

        public void add(int index, ListTreeNode element) {
            if (hasElement || index > 0)
                throw new IllegalArgumentException();
            this.element = element;
            hasElement = true;
        }

        public boolean addAll(Collection<? extends ListTreeNode> c) {
            if (hasElement || c.size() > 1)
                throw new IllegalArgumentException();
            Iterator<? extends ListTreeNode> i = c.iterator();
            return (i.hasNext() && add(i.next()));
        }

        public boolean addAll(int index, Collection<? extends ListTreeNode> c) {
            if (hasElement || index > 0)
                throw new IllegalArgumentException();
            return addAll(c);
        }

        public void clear() {
            this.element = null;
            this.hasElement = false;
        }

        public boolean contains(Object o) {
            return hasElement ? element.equals((ListTreeNode) o) : o == null;
        }

        public boolean containsAll(Collection<?> c) {
            Iterator<?> iterator = c.iterator();
            while (iterator.hasNext()) {
                if (contains(iterator.next()))
                    continue;
                return false;
            }
            return true;
        }

        public ListTreeNode get(int index) {
            if (!hasElement || index > 0)
                throw new IndexOutOfBoundsException();
            return element;
        }

        public int indexOf(Object o) {
            return contains(o) ? 0 : -1;
        }

        public boolean isEmpty() {
            return !hasElement;
        }

        public Iterator<ListTreeNode> iterator() {

            return new Iterator<ListTreeNode>() {

                private boolean hasNext = hasElement;

                public boolean hasNext() {
                    return hasNext;
                }

                public ListTreeNode next() {
                    if (hasNext) {
                        hasNext = false;
                        return element;
                    }
                    throw new NoSuchElementException();
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public int lastIndexOf(Object o) {
            return contains(o) ? 0 : -1;
        }

        public ListIterator<ListTreeNode> listIterator() {

            return new ListIterator<ListTreeNode>() {

                private boolean hasNext = hasElement;

                public void add(ListTreeNode o) {
                    throw new UnsupportedOperationException();
                }

                public boolean hasNext() {
                    return hasNext;
                }

                public boolean hasPrevious() {
                    return !hasNext && hasElement;
                }

                public ListTreeNode next() {
                    if (hasNext) {
                        hasNext = false;
                        return element;
                    }
                    throw new NoSuchElementException();
                }

                public int nextIndex() {
                    return 0;
                }

                public ListTreeNode previous() {
                    if (!hasNext && hasElement) {
                        hasNext = true;
                        return element;
                    }
                    throw new NoSuchElementException();
                }

                public int previousIndex() {
                    return hasPrevious() ? 0 : -1;
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void set(ListTreeNode o) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public ListIterator<ListTreeNode> listIterator(int index) {
            if (!hasElement || index != 0)
                throw new IndexOutOfBoundsException();
            return listIterator();
        }


        public ListTreeNode remove(int index) {
            if (!hasElement || index != 0)
                throw new IndexOutOfBoundsException();
            ListTreeNode element = this.element;
            this.element = null;
            hasElement = false;
            return element;
        }

        public boolean remove(Object o) {
            if (contains(o)) {
                element = null;
                hasElement = false;
                return true;
            }
            return false;
        }

        public boolean removeAll(Collection<?> c) {
            if (c.size() == 1) {
                return remove(c.iterator().next());
            }
            return false;
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public ListTreeNode set(int index, ListTreeNode newElement) {
            if (index != 0)
                throw new IllegalArgumentException();
            ListTreeNode oldElement = this.element;
            hasElement = true;
            this.element = newElement;
            return oldElement;
        }

        public int size() {
            return hasElement ? 1 : 0;
        }

        public List<ListTreeNode> subList(int fromIndex, int toIndex) {
            //TODO implement subList whenever this is required.
            throw new UnsupportedOperationException();
        }

        public Object[] toArray() {
            if (hasElement) {
                Object[] objects = new Object[1];
                objects[0] = element;
                return objects;
            }
            return new Object[0];
        }

        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            if (hasElement) {
                if (!a.getClass().isAssignableFrom(element.getClass()))
                    throw new ArrayStoreException();
                if (a.length < 0)
                    a = (T[]) new Object[1];
                a[0] = (T) element;
            }
            return a;
        }
    }

    protected class ArrayListTreeCursor implements MutableListTreeCursor<E>, Cloneable {

        ArrayListTree<E> tree;

        ListTreeNode current;

        List<ListTreeNode> siblings;

        int[] positions;

        int level = -1;

        <T extends ArrayListTree<E>> ArrayListTreeCursor(T tree) {
            this.tree = tree;
            this.current = tree.root;
            positions = new int[10];
        }

        public int level() {
            return level;
        }

        @SuppressWarnings("unchecked")
        public ListTreePosition getPosition() {
            return new ArrayListTreePosition();
        }

        public void setPosition(TreePosition position) {
            reset();
            ListTreePosition.IntIterator iterator = ((ListTreePosition) position).intIterator();
            while (iterator.hasNext()) {
                down(iterator.next());
            }
        }

        public void down() {
            down(0);
        }

        public void down(int position) {
            if ((siblings = current.children) == null)
                throw new NoSuchElementException();
            int len;
            if ((len = this.positions.length) <= ++level) {
                int[] tmpPositions = new int[len + 10];
                System.arraycopy(this.positions, 0, tmpPositions, 0, len);
                this.positions = tmpPositions;
            }
            this.positions[level] = position;
            current = siblings.get(position);
        }

        public boolean hasChildren() {
            return current.children != null;
        }

        public boolean hasParent() {
            return level > 0;
        }

        public void up() {
            if ((current = current.parent) == null)
                throw new NoSuchElementException();
            if (current.parent == null) {
                siblings = null;
                level = -1;
            } else {
                siblings = current.parent.children;
                level--;
            }
        }

        public boolean hasNext() {
            return siblings == null ? false : positions[level] < siblings.size() - 1;
        }

        public boolean hasPrevious() {
            return siblings == null ? false : positions[level] > 0;
        }

        public void next() {
            int position;
            if (level < 0 || (position = ++positions[level]) >= siblings.size())
                throw new NoSuchElementException();
            current = siblings.get(position);
        }

        public void previous() {
            int position;
            if (level < 0 || (position = --positions[level]) < 0)
                throw new NoSuchElementException();
            current = siblings.get(position);
        }

        public void first() {
            positions[level] = 0;
            current = siblings.get(0);
        }

        public void last() {
            int len;
            positions[level] = (len = siblings.size() - 1);
            current = siblings.get(len);
        }

        public void reset() {
            level = -1;
            current = tree.root;
        }

        public void setElement(E element) {
            if (level < 0)
                throw new UnsupportedOperationException();
            current.element = element;
        }

        public E getElement() {
            if (level < 0)
                throw new UnsupportedOperationException();
            return current.element;
        }

        public void addChild(E element) {
            if (current.children == null)
                current.children = createChildrenList();
            current.children.add(new ListTreeNode(current, element));
        }

        public void addChild(int i, E element) {
            if (current.children == null)
                current.children = createChildrenList();
            current.children.add(i, new ListTreeNode(current, element));
        }

        @SuppressWarnings("unchecked")
        public void addAsChild(int index, Tree<E> tree) {

            final int rootIndex = index;

            try {
                walker.doWalk(tree, new TreeWalker.Walk<E>() {

                    boolean isRoot = true;

                    public void doDown(TreeCursor<E> cursor) {
                        if (isRoot) {
                            addChild(rootIndex, cursor.getElement());
                            down(rootIndex);
                            isRoot = false;
                        } else {
                            addChild(cursor.getElement());
                            down();
                        }
                    }

                    public void doNext(TreeCursor<E> cursor) {
                        up();
                        addChild(cursor.getElement());
                        down(0);
                        last();
                    }

                    public void doUp(TreeCursor<E> cursor) {
                        up();
                    }
                });
            } catch (TreeWalker.AbortProcessingException ape) {
            }
            up();
        }

        @SuppressWarnings("unchecked")
        public void addAsChild(Tree<E> tree) {
            try {
                walker.doWalk(tree, new TreeWalker.Walk<E>() {

                    boolean isRoot = true;

                    public void doDown(TreeCursor<E> cursor) {
                        if (isRoot) {
                            addChild(cursor.getElement());
                            down(0);
                            last();
                            isRoot = false;
                        } else {
                            addChild(cursor.getElement());
                            down();
                        }
                    }

                    public void doNext(TreeCursor<E> cursor) {
                        up();
                        addChild(cursor.getElement());
                        down(0);
                        last();
                    }

                    public void doUp(TreeCursor<E> cursor) {
                        up();
                    }
                });
            } catch (TreeWalker.AbortProcessingException ape) {
            }
            up();
        }

        public MutableListTree<E> remove() {
            if (level < 0)
                throw new NoSuchElementException();
            siblings.remove(positions[level]);
            if (siblings.size() == 0)
                current.parent.children = null;
            siblings = null;
            return new ArrayListTree<E>(current);
        }

        public void clear() {
            current.clear();
        }

        @SuppressWarnings("unchecked")
        public MutableListTreeCursor<E> clone() {
            try {
                return (MutableListTreeCursor<E>) super.clone();
            } catch (CloneNotSupportedException cnse) {
                throw new InternalError();
            }
        }

        List<ListTreeNode> createChildrenList() {
            if (level < 0) {
                return new SingleListTreeNodeList();
            } else {
                return new ArrayList<ListTreeNode>();
            }
        }

        class ArrayListTreePosition implements ListTreePosition {

            ArrayListTreePosition() {
                int len;
                path = new int[len = level + 1];
                if (level >= 0) {
                    System.arraycopy(positions, 0, path, 0, len);
                }
            }

            int[] path;

            public ListTreePosition.IntIterator intIterator() {
                return new ArrayListTreePositionIntIterator();
            }

            class ArrayListTreePositionIntIterator implements ListTreePosition.IntIterator {

                int position = 0;

                public boolean hasNext() {
                    return (position < path.length);
                }

                public int next() {
                    return path[position++];
                }
            }

            public String toString() {
                if (path.length == 0) return "";
                StringBuilder builder = new StringBuilder(path.length * 2);
                for (int next : path) {
                    builder.append(next).append(',');
                }
                return builder.substring(0, builder.length() - 1);
            }
        }
    }
}
