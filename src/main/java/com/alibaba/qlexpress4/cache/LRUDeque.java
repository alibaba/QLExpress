package com.alibaba.qlexpress4.cache;

import java.util.*;

/**
 * @Author TaoKan
 * @Date 2022/6/5 下午3:08
 */
public class LRUDeque<E extends Linked<E>> extends AbstractCollection<E> implements Deque<E> {

    E first;
    E last;

    void linkFirst(final E e) {
        final E f = first;
        first = e;

        if (f == null) {
            last = e;
        } else {
            f.setPrevious(e);
            e.setNext(f);
        }
    }

    void linkLast(final E e) {
        final E l = last;
        last = e;

        if (l == null) {
            first = e;
        } else {
            l.setNext(e);
            e.setPrevious(l);
        }
    }

    E unlinkFirst() {
        final E f = first;
        final E next = f.getNext();
        f.setNext(null);

        first = next;
        if (next == null) {
            last = null;
        } else {
            next.setPrevious(null);
        }
        return f;
    }

    E unlinkLast() {
        final E l = last;
        final E prev = l.getPrevious();
        l.setPrevious(null);
        last = prev;
        if (prev == null) {
            first = null;
        } else {
            prev.setNext(null);
        }
        return l;
    }

    void unlink(E e) {
        final E prev = e.getPrevious();
        final E next = e.getNext();

        if (prev == null) {
            first = next;
        } else {
            prev.setNext(next);
            e.setPrevious(null);
        }

        if (next == null) {
            last = prev;
        } else {
            next.setPrevious(prev);
            e.setNext(null);
        }
    }

    @Override
    public boolean isEmpty() {
        return (first == null);
    }

    void checkNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }


    @Override
    public int size() {
        int size = 0;
        for (E e = first; e != null; e = e.getNext()) {
            size++;
        }
        return size;
    }

    @Override
    public void clear() {
        for (E e = first; e != null; ) {
            E next = e.getNext();
            e.setPrevious(null);
            e.setNext(null);
            e = next;
        }
        first = last = null;
    }

    @Override
    public boolean contains(Object o) {
        return (o instanceof Linked<?>) && contains((Linked<?>) o);
    }

    boolean contains(Linked<?> e) {
        return (e.getPrevious() != null)
                || (e.getNext() != null)
                || (e == first);
    }

    public void moveToFront(E e) {
        if (e != first) {
            unlink(e);
            linkFirst(e);
        }
    }

    public void moveToBack(E e) {
        if (e != last) {
            unlink(e);
            linkLast(e);
        }
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public E peekFirst() {
        return first;
    }

    @Override
    public E peekLast() {
        return last;
    }

    @Override
    public E getFirst() {
        checkNotEmpty();
        return peekFirst();
    }

    @Override
    public E getLast() {
        checkNotEmpty();
        return peekLast();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public boolean offerFirst(E e) {
        if (contains(e)) {
            return false;
        }
        linkFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        if (contains(e)) {
            return false;
        }
        linkLast(e);
        return true;
    }

    @Override
    public boolean add(E e) {
        return offerLast(e);
    }


    @Override
    public void addFirst(E e) {
        if (!offerFirst(e)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void addLast(E e) {
        if (!offerLast(e)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        return isEmpty() ? null : unlinkFirst();
    }

    @Override
    public E pollLast() {
        return isEmpty() ? null : unlinkLast();
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        return (o instanceof Linked<?>) && remove((E) o);
    }

    // A fast-path removal
    boolean remove(E e) {
        if (contains(e)) {
            unlink(e);
            return true;
        }
        return false;
    }

    @Override
    public E removeFirst() {
        checkNotEmpty();
        return pollFirst();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public E removeLast() {
        checkNotEmpty();
        return pollLast();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            modified |= remove(o);
        }
        return modified;
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public Iterator<E> iterator() {
        return new AbstractLinkedIterator(first) {
            @Override
            E computeNext() {
                return cursor.getNext();
            }
        };
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new AbstractLinkedIterator(last) {
            @Override
            E computeNext() {
                return cursor.getPrevious();
            }
        };
    }

    abstract class AbstractLinkedIterator implements Iterator<E> {
        E cursor;

        AbstractLinkedIterator(E start) {
            cursor = start;
        }

        @Override
        public boolean hasNext() {
            return (cursor != null);
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E e = cursor;
            cursor = computeNext();
            return e;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Retrieves the next element to traverse to or <tt>null</tt> if there are
         * no more elements.
         */
        abstract E computeNext();
    }
}

/**
 * An element that is linked on the {@link Deque}.
 */
interface Linked<T extends Linked<T>> {

    /**
     * Retrieves the previous element or <tt>null</tt> if either the element is
     * unlinked or the first element on the deque.
     */
    T getPrevious();

    /**
     * Sets the previous element or <tt>null</tt> if there is no link.
     */
    void setPrevious(T prev);

    /**
     * Retrieves the next element or <tt>null</tt> if either the element is
     * unlinked or the last element on the deque.
     */
    T getNext();

    /**
     * Sets the next element or <tt>null</tt> if there is no link.
     */
    void setNext(T next);
}
