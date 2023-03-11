package com.alibaba.qlexpress4.parser;

/**
 * TODO: 性能比较
 * linked list to optimize instruction generator
 * which sub list can only be added once
 * Author: DQinYuan
 */
public class QList<T> {

    private QNode<T> head;

    private QNode<T> tail;

    private static class QNode<T> {
        private final T value;
        private QNode<T> next;

        private QNode(T value) {
            this.value = value;
        }
    }

    private QList(QNode<T> head, QNode<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    public static <T> QList<T> emptyList() {
        return new QList<>(null, null);
    }

    public static <T> QList<T> fromList(QList<T> originList) {
        return new QList<>(originList.head, originList.tail);
    }

    public static <T> QList<T> singletonList(T value) {
        QNode<T> head = new QNode<>(value);
        return new QList<>(head, head);
    }

    public QList<T> addLast(T item) {
        QNode<T> newTail = new QNode<>(item);
        if (this.head == null) {
            this.head = newTail;
        } else {
            this.tail.next = newTail;
        }
        this.tail = newTail;
        return this;
    }

    public QList<T> addAll(QList<T> subList) {
        if (subList.head == null) {
            // add empty list
            return this;
        } else if (this.head == null) {
            this.head = subList.head;
            this.tail = subList.tail;
        } else {
            this.tail.next = subList.head;
            this.tail = subList.tail;
        }
        return this;
    }

    public T[] toArray(T[] container) {
        QNode<T> cur = this.head;
        for (int i = 0; i < container.length; i++) {
            container[i] = cur.value;
            cur = cur.next;
        }
        return container;
    }

    public int getSize() {
        QNode<T> cur = this.head;
        int size = 0;
        while (cur != null) {
            size++;
            cur = cur.next;
        }
        return size;
    }
}
