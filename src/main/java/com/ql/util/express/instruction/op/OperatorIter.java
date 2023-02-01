package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

import java.util.Iterator;

public class OperatorIter extends Operator {
    public OperatorIter(String name) {
        this.name = name;
    }

    public OperatorIter(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0]);
    }

    public Object executeInner(Object op) throws Exception {
        if (op instanceof Iterable<?>) {
            return ((Iterable<?>) op).iterator();
        } else if (op.getClass().isArray()) {
            return new SimpleArrayIterator((Object[]) op);
        } else {
            throw new IllegalArgumentException("对象类型 \"" + op.getClass() + "\" 不是iterable或array");
        }
    }

    private static class SimpleArrayIterator implements Iterator<Object> {

        private final Object[] array;

        private int position;

        SimpleArrayIterator(Object[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return position < array.length;
        }

        @Override
        public Object next() {
            return array[position++];
        }
    }
}
