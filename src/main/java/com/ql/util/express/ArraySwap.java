package com.ql.util.express;

public final class ArraySwap {
    private OperateData[] operateDataArray;
    private int start;
    public int length;

    public void swap(OperateData[] operateDataArray, int start, int length) {
        this.operateDataArray = operateDataArray;
        this.start = start;
        this.length = length;
    }

    public OperateData get(int i) {
        return this.operateDataArray[i + start];
    }
}
