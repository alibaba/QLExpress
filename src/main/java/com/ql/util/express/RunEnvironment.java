package com.ql.util.express;

public final class RunEnvironment {
    private static final int INIT_DATA_LENGTH = 15;
    private boolean isTrace;
    private int point = -1;
    int programPoint = 0;
    private OperateData[] dataContainer;
    private final ArraySwap arraySwap = new ArraySwap();

    private boolean isExit = false;
    private Object returnValue = null;

    private InstructionSet instructionSet;
    private InstructionSetContext context;

    public RunEnvironment(InstructionSet instructionSet, InstructionSetContext instructionSetContext, boolean isTrace) {
        dataContainer = new OperateData[INIT_DATA_LENGTH];
        this.instructionSet = instructionSet;
        this.context = instructionSetContext;
        this.isTrace = isTrace;
    }

    public void initial(InstructionSet instructionSet, InstructionSetContext instructionSetContext, boolean isTrace) {
        this.instructionSet = instructionSet;
        this.context = instructionSetContext;
        this.isTrace = isTrace;
    }

    public void clear() {
        isTrace = false;
        point = -1;
        programPoint = 0;

        isExit = false;
        returnValue = null;

        instructionSet = null;
        context = null;
    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    public InstructionSetContext getContext() {
        return this.context;
    }

    public void setContext(InstructionSetContext instructionSetContext) {
        this.context = instructionSetContext;
    }

    public boolean isExit() {
        return isExit;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object value) {
        this.returnValue = value;
    }

    public void quitExpress(Object returnValue) {
        this.isExit = true;
        this.returnValue = returnValue;
    }

    public void quitExpress() {
        this.isExit = true;
        this.returnValue = null;
    }

    public boolean isTrace() {
        return this.isTrace;
    }

    public int getProgramPoint() {
        return programPoint;
    }

    public void programPointAddOne() {
        programPoint++;
    }

    public void gotoLastWhenReturn() {
        programPoint = this.instructionSet.getInstructionLength();
    }

    public int getDataStackSize() {
        return this.point + 1;
    }

    public void push(OperateData data) {
        this.point++;
        if (this.point >= this.dataContainer.length) {
            ensureCapacity(this.point + 1);
        }
        this.dataContainer[point] = data;
    }

    public OperateData peek() {
        if (point < 0) {
            throw new RuntimeException("系统异常，堆栈指针错误");
        }
        return this.dataContainer[point];
    }

    public OperateData pop() {
        if (point < 0) {
            throw new RuntimeException("系统异常，堆栈指针错误");
        }
        OperateData result = this.dataContainer[point];
        this.point--;
        return result;
    }

    public void clearDataStack() {
        this.point = -1;
    }

    public void gotoWithOffset(int offset) {
        this.programPoint = this.programPoint + offset;
    }

    /**
     * 此方法是调用最频繁的，因此尽量精简代码，提高效率
     *
     * @param len
     * @return
     */
    public ArraySwap popArray(int len) {
        int start = point - len + 1;
        this.arraySwap.swap(this.dataContainer, start, len);
        point = point - len;
        return this.arraySwap;
    }

    public void ensureCapacity(int minCapacity) {
        int oldCapacity = this.dataContainer.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            OperateData[] tempList = new OperateData[newCapacity];
            System.arraycopy(this.dataContainer, 0, tempList, 0, oldCapacity);
            this.dataContainer = tempList;
        }
    }
}
