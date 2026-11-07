package com.alibaba.qlexpress4.pf4j;

public class SameNamedComparable implements Comparable<SameNamedComparable> {
    
    private final int value;
    
    public SameNamedComparable(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public int compareTo(SameNamedComparable o) {
        return Integer.compare(value, o.value);
    }
}
