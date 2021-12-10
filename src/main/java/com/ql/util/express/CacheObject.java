package com.ql.util.express;

/**
 * 简单的缓存对象
 *
 * @author tianqiao
 */
public class CacheObject {
    private String expressName;

    private String text;

    private InstructionSet instructionSet;

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String name) {
        this.expressName = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    public void setInstructionSet(InstructionSet instructionSet) {
        this.instructionSet = instructionSet;
    }
}
