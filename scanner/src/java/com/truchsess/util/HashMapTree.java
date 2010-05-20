package com.truchsess.util;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * an implementation of MapTree<E> and MutableTree<E> that
 * stores references to child-nodes in HashMaps
 *
 * @author Norbert von Truchsess
 * @param <K> runtime type of keys
 * @param <E> runtime type of elements to store in tree-nodes
 * @see java.util.HashMap
 */
public class HashMapTree<K, E> implements MutableMapTree<K, E>, Serializable {

    static final long serialVersionUID = 1934875163289230234L;

    MapTreeNode root;

    @SuppressWarnings("unchecked")
    private static final TreeWalker walker = new TreeWalkerBase();

    public HashMapTree() {
        this.root = new MapTreeNode();
    }

    private HashMapTree(K key, MapTreeNode node) {
        super();
        root.children = new SingleMapTreeNodeMap();
        root.children.put(key, node);
        node.parent = root;
    }

    public MutableMapTreeCursor<K, E> getCursor() {
        return new HashMapTreeCursor(this);
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        try {
            walker.doWalk(this, new TreeWalker.Walk<E>() {

                public void doDown(TreeCursor<E> cursor) {
                }

                public void doNext(TreeCursor<E> cursor) {
                }

                public void doUp(TreeCursor<E> cursor) {
                    ((MutableTreeCursor<E>) cursor).clear();
                }
            });
        } catch (TreeWalker.AbortProcessingException ape) {
        }
    }

    protected class MapTreeNode {

        MapTreeNode parent;

        Map<K, MapTreeNode> children;

        E element;

        MapTreeNode() {
        }

        MapTreeNode(MapTreeNode parent, E element) {
            this.parent = parent;
            this.element = element;
        }

        void clear() {
            if (children != null) {
                if (!children.isEmpty()) {
                    for (MapTreeNode node : children.values()) {
                        node.clear();
                    }
                    children.clear();
                }
                children = null;
            }
            element = null;
            parent = null;
        }
    }

    protected class HashMapTreeCursor implements MutableMapTreeCursor<K, E>, Cloneable {

        HashMapTree<K, E> tree;

        MapTreeNode currentNode;

        Map<K, MapTreeNode> siblings;

        K[] keys;

        Iterator<Map.Entry<K, MapTreeNode>>[] nodes;

        int level = -1;

        @SuppressWarnings("unchecked")
                <T extends HashMapTree<K, E>> HashMapTreeCursor(T tree) {
            this.tree = tree;
            this.currentNode = tree.root;
            keys = (K[]) new Object[10];
            nodes = new Iterator[10];
            //nodes = (Iterator<Map.Entry<K,MapTreeNode>>[])new Object[10];
        }


        public int level() {
            return level;
        }

        @SuppressWarnings("unchecked")
        public MapTreePosition<K> getPosition() {

            int len = level + 1;
            final K[] path = (K[]) new Object[len];
            if (level >= 0)
                System.arraycopy(keys, 0, path, 0, len);

            return new MapTreePosition<K>() {

                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        int position = 0;

                        public boolean hasNext() {
                            return (position < path.length);
                        }

                        public K next() {
                            return path[position++];
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                public String toString() {
                    if (path.length == 0) return "";
                    StringBuilder builder = new StringBuilder(path.length * 2);
                    for (K next : path) {
                        builder.append(next).append(',');
                    }
                    return builder.substring(0, builder.length() - 1);
                }
            };
        }

        @SuppressWarnings("unchecked")
        public void setPosition(TreePosition position) {
            Iterator<K> iterator = ((MapTreePosition<K>) position).iterator();
            while (iterator.hasNext()) {
                down(iterator.next());
            }
        }

        public void next() {
            Iterator<Map.Entry<K, MapTreeNode>> iterator;
            if ((iterator = nodes[level]) == null) {
                iterator = (nodes[level] = siblings.entrySet().iterator());
                K key;
                if ((key = keys[level]) != null) {
                    while (iterator.hasNext()) {
                        if (!iterator.next().getKey().equals(key))
                            continue;
                    }
                }
            }
            Map.Entry<K, MapTreeNode> entry = iterator.next();
            keys[level] = entry.getKey();
            currentNode = entry.getValue();
        }

        public boolean hasNext() {
            Iterator<Map.Entry<K, MapTreeNode>> iterator;
            if ((iterator = nodes[level]) == null) {
                iterator = (nodes[level] = siblings.entrySet().iterator());
                K key;
                if ((key = keys[level]) != null) {
                    while (iterator.hasNext()) {
                        if (!iterator.next().getKey().equals(key))
                            continue;
                    }
                }
            }
            return iterator.hasNext();
        }

        public void down() {
            if ((siblings = currentNode.children) == null)
                throw new NoSuchElementException();
            Map.Entry<K, MapTreeNode> entry = (nodes[++level] = siblings.entrySet().iterator()).next();
            keys[level] = entry.getKey();
            currentNode = entry.getValue();

        }

        @SuppressWarnings("unchecked")
        public void down(K key) {
            if ((siblings = currentNode.children) == null)
                throw new NoSuchElementException();
            int len;
            if ((len = keys.length) <= ++level) {
                K[] tmpKeys = (K[]) new Object[len + 10];
                Iterator<Map.Entry<K, MapTreeNode>>[] tmpNodes = (Iterator<Map.Entry<K, MapTreeNode>>[]) new Object[len + 10];
                System.arraycopy(keys, 0, tmpKeys, 0, len);
                System.arraycopy(nodes, 0, tmpNodes, 0, len);
                keys = tmpKeys;
                nodes = tmpNodes;
            }
            keys[level] = key;
            currentNode = currentNode.children.get(key);
        }

        public boolean hasChildren() {
            return currentNode.children != null;
        }

        public boolean hasParent() {
            return currentNode.parent != null;
        }

        public void up() {
            if (currentNode.parent == null)
                throw new NoSuchElementException();
            currentNode = currentNode.parent;
            nodes[level] = null;
            keys[level--] = null;
        }

        public void reset() {
            level = -1;
            currentNode = tree.root;
        }

        public void setElement(E element) {
            currentNode.element = element;
        }

        public E getElement() {
            return (currentNode != null ? currentNode.element : null);
        }

        public Set<K> getChildKeys() {
            return currentNode.children == null ? new HashSet<K>() : currentNode.children.keySet();
        }

        public Entry<K, E> getEntry() {
            return new MapTreeEntry<E>(getKey(), currentNode.element);
        }

        class MapTreeEntry<V> implements Entry<K, V> {

            K key;
            V value;

            MapTreeEntry(K key, V value) {
                this.key = key;
                this.value = value;
            }

            public K getKey() {
                return this.key;
            }

            public V getValue() {
                return this.value;
            }

            public V setValue(V value) {
                V retval = this.value;
                this.value = value;
                return retval;
            }
        }

        public K getKey() {
            return level == -1 ? null : keys[level];
        }

        public void putChild(K key, E element) {
            if (currentNode.children == null)
                currentNode.children = new HashMap<K, MapTreeNode>();
            currentNode.children.put(key, new MapTreeNode(currentNode, element));
        }

        public void putAsChild(K key, MapTree<K, E> tree) {

            final K rootkey = key;

            try {
                MapTreeWalker.doWalk(tree, new MapTreeWalker.Walk<K, E>() {

                    boolean isRoot = true;

                    public void doDown(MapTreeCursor<K, E> cursor) {
                        K key;
                        if (isRoot) {
                            key = rootkey;
                            isRoot = false;
                        } else {
                            key = cursor.getKey();
                        }
                        putChild(key, cursor.getElement());
                        down(key);
                    }

                    public void doUp(MapTreeCursor<K, E> cursor) {
                        up();
                    }
                });
            } catch (MapTreeWalker.AbortProcessingException ape) {
            }
        }

        public MutableMapTree<K, E> remove() {
            K key = keys[--level];
            currentNode.parent.children.remove(key);
            currentNode.parent = null;
            return new HashMapTree<K, E>(key, currentNode);
        }

        public void clear() {
            currentNode.clear();
        }

        @SuppressWarnings("unchecked")
        public MutableMapTreeCursor<K, E> clone() {
            try {
                return (MutableMapTreeCursor<K, E>) super.clone();
            } catch (CloneNotSupportedException cnse) {
                throw new InternalError();
            }
        }
    }

    protected class SingleMapTreeNodeMap implements Map<K, MapTreeNode> {

        private boolean hasElement = false;

        private K key;
        private MapTreeNode element;

        public void clear() {
            hasElement = false;
            key = null;
            element = null;
        }

        public boolean containsKey(Object key) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean containsValue(Object value) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public Set<Map.Entry<K, MapTreeNode>> entrySet() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public MapTreeNode get(Object key) {
            return this.key.equals(key) ? element : null;
        }

        public boolean isEmpty() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public Set<K> keySet() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public MapTreeNode put(K key, MapTreeNode element) {
            MapTreeNode oldElement = null;
            if (this.key.equals(key)) {
                oldElement = this.element;
            } else {
                this.key = key;
            }
            this.element = element;
            return oldElement;
        }

        @SuppressWarnings("unchecked")
        public void putAll(Map t) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public MapTreeNode remove(Object key) {
            if (this.key.equals(key)) {
                MapTreeNode oldElement = element;
                hasElement = false;
                element = null;
                key = null;
                return oldElement;
            }
            return null;
        }

        public int size() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public Collection<MapTreeNode> values() {

            return new Collection<MapTreeNode>() {

                public boolean add(MapTreeNode o) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public boolean addAll(Collection<? extends MapTreeNode> c) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public void clear() {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public boolean contains(Object o) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public boolean containsAll(Collection<?> c) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public boolean isEmpty() {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public Iterator<MapTreeNode> iterator() {

                    return new Iterator<MapTreeNode>() {

                        boolean hasNext = hasElement;

                        public boolean hasNext() {
                            return hasNext;
                        }

                        public MapTreeNode next() {
                            hasNext = false;
                            return element;
                        }

                        public void remove() {
                            // TODO Auto-generated method stub
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                public boolean remove(Object o) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public boolean removeAll(Collection<?> c) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public boolean retainAll(Collection<?> c) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public int size() {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public Object[] toArray() {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

                public <T> T[] toArray(T[] a) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }

            };
        }
    }
}
