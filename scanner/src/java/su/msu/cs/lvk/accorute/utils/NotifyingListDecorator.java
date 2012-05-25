package su.msu.cs.lvk.accorute.utils;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.list.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 04.05.12
 * Time: 0:55
 * To change this template use File | Settings | File Templates.
 */
public class NotifyingListDecorator<T> extends CallbackContainer implements List<T>{
    private final List<T> wrappedList;
    public class NotifyingListIteratorDecorator extends CallbackContainer implements ListIterator<T>{
        private final ListIterator<T> wrappedListIterator;
        NotifyingListIteratorDecorator(ListIterator<T> wrappedListIterator) {
            this.wrappedListIterator = wrappedListIterator;
        }
        public boolean hasNext() {
            return wrappedListIterator.hasNext(); 
        }

        public T next() {
            return wrappedListIterator.next(); 
        }

        public boolean hasPrevious() {
            return wrappedListIterator.hasPrevious(); 
        }

        public T previous() {
            return wrappedListIterator.previous(); 
        }

        public int nextIndex() {
            return wrappedListIterator.nextIndex(); 
        }

        public int previousIndex() {
            return wrappedListIterator.previousIndex(); 
        }

        public void remove() {
            super.notifyCallbacks();
            wrappedListIterator.remove();
        }

        public void set(T t) {
            super.notifyCallbacks();
            wrappedListIterator.set(t);
        }

        public void add(T t) {
            super.notifyCallbacks();
            wrappedListIterator.add(t);
        }
    }
    public NotifyingListDecorator(List list) {
        wrappedList = list;
    }
    public T set(int index, T object) {
        super.notifyCallbacks();
        return wrappedList.set(index, object);  
    }
    public T remove(int index) {
        super.notifyCallbacks();
        return wrappedList.remove(index);  
    }
    public int indexOf(Object o) {
        return wrappedList.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return wrappedList.lastIndexOf(o);
    }
    public void add(int index, T object) {
        super.notifyCallbacks();
        wrappedList.add(index, object);  
    }
    public boolean addAll(int index, Collection coll) {
        super.notifyCallbacks();
        return wrappedList.addAll(index, coll);  
    }
    public boolean addAll(Collection coll) {
        super.notifyCallbacks();
        return wrappedList.addAll(coll);  
    }
    public int size() {
        return wrappedList.size();
    }

    public boolean isEmpty() {
        return wrappedList.isEmpty();
    }

    public boolean contains(Object o) {
        return wrappedList.contains(o);
    }

    public Iterator<T> iterator() {
        return wrappedList.iterator();
    }

    public Object[] toArray() {
        return wrappedList.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return wrappedList.toArray(a);
    }

    public boolean add(T object) {
        super.notifyCallbacks();
        return wrappedList.add(object);  
    }
    public void clear() {
        super.notifyCallbacks();
        wrappedList.clear();  
    }
    public T get(int index) {
        return wrappedList.get(index);
    }
    public ListIterator<T> listIterator(int index) {
        NotifyingListIteratorDecorator iter = new NotifyingListIteratorDecorator(wrappedList.listIterator(index));
        iter.addCallback(new Callback0() {
            public void CallMeBack() {
                notifyCallbacks();
            }
        });
        return iter;
    }
    public List<T> subList(int fromIndex, int toIndex) {
        return wrappedList.subList(fromIndex,toIndex);
    }
    public ListIterator<T> listIterator() {
        NotifyingListIteratorDecorator iter = new NotifyingListIteratorDecorator(wrappedList.listIterator());
        iter.addCallback(new Callback0() {
            public void CallMeBack() {
                notifyCallbacks();
            }
        });
        return iter;  
    }
    public boolean remove(Object object) {
        return wrappedList.remove(object);  
    }

    public boolean containsAll(Collection<?> c) {
        return wrappedList.containsAll(c);
    }
    public boolean removeAll(Collection coll) {
        return wrappedList.removeAll(coll);  
    }
    public boolean retainAll(Collection coll) {
        return wrappedList.retainAll(coll);  
    }
}
