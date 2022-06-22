package com.alibaba.qlexpress4.cache;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * AbstractUnmodifiableIterator
 * @Author TaoKan
 * @Date 2022/6/11 下午8:05
 * test
 */
public abstract class AbstractUnmodifiableIterator<E> implements Iterator<E> {
    private E next;

    protected AbstractUnmodifiableIterator(E firstEntity) {
        this.next = firstEntity;
    }
    public final boolean hasNext() {
        return next != null;
    }

    @Override
    public final E next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        E old = next;
        next = computeNext(old);
        return old;
    }

    protected abstract E computeNext(E previous);
}
