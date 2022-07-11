package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public class FixedSizeStack {

    private final Value[] elements;

    private int cursor = 0;

    public FixedSizeStack(int size){
        this.elements = new Value[size];
    }

    public void push(Value ele) {
        elements[cursor++] = ele;
    }

    public Value pop() {
        return elements[--cursor];
    }

    public Parameters pop(int n) {
        cursor -= n;
        return new StackSwapParameters(elements, cursor, n);
    }

    private static class StackSwapParameters implements Parameters {

        private final Value[] elements;

        private final int start;

        private final int length;

        private StackSwapParameters(Value[] elements, int start, int length) {
            this.elements = elements;
            this.start = start;
            this.length = length;
        }

        @Override
        public Value get(int i) {
            return i >= length? null: elements[start + i];
        }

        @Override
        public int size() {
            return length;
        }
    }

}
